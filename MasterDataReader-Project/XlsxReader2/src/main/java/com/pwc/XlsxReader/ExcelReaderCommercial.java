package com.pwc.XlsxReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.pwc.XlsxReader.entity.BackYardConstruction;
import com.pwc.XlsxReader.entity.DrawingNumber;
import com.pwc.XlsxReader.entity.DxfConstent;
import com.pwc.XlsxReader.entity.Far;
import com.pwc.XlsxReader.entity.JobNumber;
import com.pwc.XlsxReader.entity.Master;
import com.pwc.XlsxReader.entity.MyShorting;
import com.pwc.XlsxReader.entity.NoOfStory;
import com.pwc.XlsxReader.entity.PermissibleBuildingHeight;
import com.pwc.XlsxReader.entity.SetBack;

public class ExcelReaderCommercial {

	public static final String SHEET_NAME_MASTER = "MASTER";
	public static final String SHEET_NAME_FAR = "FAR";
	public static final String SHEET_NAME_NO_OF_STOREYS = "No of storeys";
	public static final String SHEET_NAME_BACK_YARD_COMSTRUCTIONS = "Back Yard construction";
	public static final String SHEET_NAME_SETBACKS = "Setbacks";
	public static final String SHEET_NAME_PERMISSIBLE_BUILDING_HEIGHT = "Height";
//	public static final String SAMPLE_XLSX_FILE_PATH = "C:\\Workspace\\Chandigarh\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\REVISED MASTER DATA FOR 12 SECTORS-new.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH = "C:\\Workspace\\Chandigarh\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\MASTER DATA FOR COMBINED PLOT_6sep21 Commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Workspace\\Chandigarh\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\Updated Mater data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\Mater data 05-08-22 -industrial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\Master data 23D, & 7-1-1608-residential.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\MASTER DATA TO UPDATE 26, 20-commertial-0209.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\GOLF-0709-commertial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\Master data 12-09-2022-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\MASTER DATA 17-09-2022-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\MASTER DATA 21-09-2022-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\4-10-2022 Master data-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\12-10-2022 Master data-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\master data 18-10-2022-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\master data-25-10-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\Master data 27-10-2022-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\Parking master data-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\Booth master data 2-11-2022-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\Master data 14-11-2022-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\NAC MANIMAJARA AND 17B 17-11-2022-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\master data 25-11-2022-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\Mater data 6-12-2022-commercial.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\Master data 18-12-12022-com.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "D:\\Chandigarh_new\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\20-12-22 Master data-com.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Workspace\\Chandigarh\\OBPS-Chandigarh\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\22-12-22_Master_data_Com.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Workspace\\Chandigarh\\OBPS-Chandigarh\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\23-12-2022_commercial_master_data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Workspace\\CHD\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\updated\\Residential master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Workspace\\Chandigarh\\OBPS-Chandigarh\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\02-01-2023 Educational occupancy master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Workspace\\Chandigarh\\OBPS-Chandigarh\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\02-01-2023 Updated society master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Workspace\\Chandigarh\\OBPS-Chandigarh\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\03-01-2023 commercial master data1.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Workspace\\Chandigarh\\OBPS-Chandigarh\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\10.1.23_Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Workspace\\Chandigarh\\OBPS-Chandigarh\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\18-01-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Workspace\\Chandigarh\\OBPS-Chandigarh\\OBPS-Chandigarh_Impl1\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\18-01-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\30-01-2023 Master data for 30-1-23.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\01-02-2023 Master data 1-02-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\20-02-2023 Zoning Plans (1).xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\27-02-2023 Residential master data 24-02-23.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\02-03-2023 Residential master data 02-03-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\06-03-2023 Master data 06-03-23.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\09-03-2023 Boothno. 206 master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\14-03-2023 Master data 09-03-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\15-03-2023 Master data 09-03-2023_2.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\20-03-2023 MASTER DATA 20-03-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\22-03-2023 Master data 22-03-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\27-03-2023 Master data 27-03-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\28-03-2023 Master data 27-03-2023 (1).xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\29-03-2023 Master data 29-03-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\06-03-2023 Master data 06-04-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\10-04-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\12-04-2023 MASTER DATA 12-04-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\17-04-23 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\20-4-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\25-04-23 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\26-04-23 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\27-04-23 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\01-05-2023 Mater data ind.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\04-05-2023 Mater data 04-05-2022.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\08-05-2023 Master data 8-05-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\09-05-2023 MASTER DATA 09-05-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\15-05-2023 MASTER DATA 15-05-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\19-05-2023 MASTER DATA 19-05-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\23-05-2023 MASTER DATA(1).xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\29-05-2023 Master data 29-05-23.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\02-06-2023 MASTER DATA.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\08-06-2023 commercial master data 08-06-2023_2.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\13-06-2023 Master data 34 .xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\16-06-2023 Mater data 15-06-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\19-06-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\27-06-2022 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\03-07-2023 Master data 03-07-2023.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\05-07-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\06-07-2023 Mater data .xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\17-07-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\20-07-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\26-07-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\27-07-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\28-07-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\03-08-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\08-08-2023 Sector 15 D Master Data v3.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\09-08-2023 MASTER DATA.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\16-08-2023 47 C Master Data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\21-08-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\24-08-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\04-09-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\08-09-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\12-09-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\14-09-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\20-09-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\22-09-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\29-09-2023 Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\06-10-2023 Post off Master data.xlsx";
//	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\11-10-2023 Master data 11-10-2023.xlsx";
	public static final String SAMPLE_XLSX_FILE_PATH  = "C:\\Users\\vmodisauser05\\Od_Workspaces\\Chd_Workspace_Develop\\OBPS-New\\MasterDataReader-Project\\XlsxReader2\\src\\main\\java\\com\\pwc\\xlsx\\11-10-2023 Master data 11-10-2023 (updated).xlsx";
	
	
	
	
	
	
	
	public static void main(String[] args) throws IOException, InvalidFormatException {
		System.out.println("==================Start===================");
		Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));
		Sheet sheet = workbook.getSheetAt(0);
		processMaster(sheet);
		workbook.close();
		System.out.println("===================End===================");
	}

	private static void processMaster(Sheet sheet) {

		DataFormatter dataFormatter = new DataFormatter();
		List<Master> masters = new ArrayList<Master>();
		for (Row row : sheet) {
			if (row.getRowNum() == 0)
				continue;
			char key = 65;
			Master master = new Master();
			for (Cell cell : row) {
				String cellValue = dataFormatter.formatCellValue(cell);
				cellValue = DxfConstent.validateValue(cellValue);
				switch (key) {
				case 'A':
					break;
				case 'D':
					master.setCode(cellValue);
					break;
				case 'F':
					master.setSector(cellValue);
					break;
				case 'G':
					master.setPlotNo(cellValue);
					break;
//				case 'H':
//					master.setBackCourtyardConstructionWidth(cellValue);
//					break;
//				case 'I':
//					master.setBackCourtyardConstructionHeight(cellValue);
//					break;
//				case 'J':
//					master.setPlotAreaInSqm(cellValue);
//					break;
//				case 'K':
//					master.setPlotAreaInSqy(cellValue);
//					break;
//				case 'L':
//					master.setAreaType(cellValue);
//					break;
//				case 'M':
//					master.setPlotDepth(cellValue);
//					break;
//				case 'N':
//					master.setPlotWidth(cellValue);
//					break;
//				case 'O':
//					master.setPermissibleBuildingStories(cellValue);
//					break;
//				case 'P':
//					master.setPermissibleBuildingHeight(cellValue);
//					break;
//				case 'Q':
//					master.setMaxmimumPermissibleFAR(cellValue);
//					break;
//				case 'R':
//					master.setMinimumPermissibleSetback_Front(cellValue);
//					break;
//				case 'S':
//					master.setMinimumPermissibleSetback_Rear(cellValue);
//					break;
//				case 'T':
//					master.setMinimumPermissibleSetback_left(cellValue);
//					break;
//				case 'U':
//					master.setMinimumPermissibleSetback_Right(cellValue);
//					break;
				case 'V':
					master.setDrawingNumber(cellValue);
					break;
				case 'W':
					master.setJobNumber(cellValue);
					break;
				default:
					break;
				}

				key++;
			}
			master.setKey(master.getCode() + "." + master.getSector() + "." + master.getPlotNo());
			masters.add(master);

		}

		processDrawingNumber(masters);
		processJobNumber(masters);
	}

	private static void processDrawingNumber(List<Master> masters) {
		System.out.println("----------------start DrawingNumber ------------------");
		List<DrawingNumber> dns = new ArrayList<DrawingNumber>();

		for (Master master : masters) {
			DrawingNumber dn = new DrawingNumber();
			dn.setKey(DrawingNumber.DRAWING_NUMBER_PREFIX + "." + master.getKey());
			dn.setDrawingNumber(master.getDrawingNumber());
			dns.add(dn);
		}

		HashMap<String, String> properties = new HashMap<String, String>();
		for (DrawingNumber dn : dns) {
			String s = properties.put(dn.getKey(), dn.getDrawingNumber());
		}
		System.out.println("Master " + masters.size() + " | properties " + properties.size());
		Utill.writeToPropertiesFile(properties, "DrawingNumber.properties");
		System.out.println("----------------end DrawingNumber ------------------");
	}

	private static void processJobNumber(List<Master> masters) {
		System.out.println("----------------start JobNumber ------------------");
		List<JobNumber> jns = new ArrayList<JobNumber>();

		for (Master master : masters) {
			JobNumber jn = new JobNumber();
			jn.setKey(JobNumber.JOB_NUMBER_PREFIX + "." + master.getKey());
			jn.setJobNumber(master.getJobNumber());
			jns.add(jn);
		}

		HashMap<String, String> properties = new HashMap<String, String>();
		for (JobNumber jn : jns) {
			String s = properties.put(jn.getKey(), jn.getJobNumber());
		}
		System.out.println("Master " + masters.size() + " | properties " + properties.size());
		Utill.writeToPropertiesFile(properties, "JobNumber.properties");
		System.out.println("----------------end JobNumber ------------------");
	}

}
