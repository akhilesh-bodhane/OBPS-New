/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.infra.notification.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PreDestroy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Publishes SMS requests to the Kafka topic consumed by the DIGIT egov-notification-sms service.
 */
@Service
public class EgovNotificationSmsProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EgovNotificationSmsProducer.class);
    private static final String EXTRA_PRODUCER_PREFIX = "sms.kafka.producer.";

    @Value("${sms.kafka.bootstrap.servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${sms.kafka.topic:egov.core.notification.sms}")
    private String topic;

    @Value("${sms.kafka.send.timeout.ms:10000}")
    private long sendTimeoutMs;

    @Value("${sms.kafka.payload.mobile.field:mobileNumber}")
    private String mobileField;

    @Value("${sms.kafka.payload.message.field:message}")
    private String messageField;

    // blank (default) = template id is not added to the payload
    @Value("${sms.kafka.payload.templateid.field:}")
    private String templateIdField;

    @Value("${sms.kafka.acks:all}")
    private String acks;

    @Autowired
    private Environment environment;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private volatile Producer<String, String> producer;

    public boolean send(final String mobileNumber, final String message) {
        return send(mobileNumber, message, null);
    }

    public boolean send(final String mobileNumber, final String message, final String templateId) {
        try {
            final Map<String, Object> body = new LinkedHashMap<>();
            body.put(mobileField, mobileNumber);
            body.put(messageField, message);
            if (!templateIdField.trim().isEmpty() && templateId != null)
                body.put(templateIdField.trim(), templateId);
            final String payload = objectMapper.writeValueAsString(body);
            getProducer().send(new ProducerRecord<>(topic, mobileNumber, payload)).get(sendTimeoutMs, TimeUnit.MILLISECONDS);
            LOGGER.info("SMS published to topic {} for mobile ending {}", topic, maskMobile(mobileNumber));
            return true;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Interrupted while publishing SMS for mobile {}", maskMobile(mobileNumber), e);
        } catch (final Exception e) {
            LOGGER.error("Error occurred while publishing SMS for mobile {}", maskMobile(mobileNumber), e);
        }
        return false;
    }

    private Producer<String, String> getProducer() {
        if (producer == null) {
            synchronized (this) {
                if (producer == null) {
                    final Properties props = new Properties();
                    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
                    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, sendTimeoutMs);
                    props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, (int) sendTimeoutMs + 5000);
                    props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, (int) sendTimeoutMs);
                    props.put(ProducerConfig.ACKS_CONFIG, acks);
                    props.putAll(extraProducerProperties());
                    producer = new KafkaProducer<>(props);
                }
            }
        }
        return producer;
    }

    /**
     * Any property named sms.kafka.producer.&lt;kafka-config-key&gt; is passed to the Kafka producer as is
     * (e.g. sms.kafka.producer.security.protocol, sms.kafka.producer.sasl.jaas.config), so security and tuning
     * can be changed from the properties file without code changes.
     */
    private Map<String, Object> extraProducerProperties() {
        final Map<String, Object> extras = new LinkedHashMap<>();
        if (environment instanceof ConfigurableEnvironment)
            for (final org.springframework.core.env.PropertySource<?> source : ((ConfigurableEnvironment) environment)
                    .getPropertySources())
                if (source instanceof EnumerablePropertySource)
                    for (final String key : ((EnumerablePropertySource<?>) source).getPropertyNames())
                        if (key.startsWith(EXTRA_PRODUCER_PREFIX) && key.length() > EXTRA_PRODUCER_PREFIX.length())
                            extras.put(key.substring(EXTRA_PRODUCER_PREFIX.length()), environment.getProperty(key));
        return extras;
    }

    private static String maskMobile(final String mobile) {
        return mobile != null && mobile.length() > 4 ? "****" + mobile.substring(mobile.length() - 4) : "****";
    }

    @PreDestroy
    public void close() {
        if (producer != null)
            producer.close();
    }
}
