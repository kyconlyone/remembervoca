package com.ihateflyingbugs.hsmd.data;

public class AsyncAddress {

	final static String index = "http://52.79.87.3/mildang/index.php/";


	public static final String Notification = 		index+"Notification";

	public static String getSplashInfo = 			index+"Authorization/getSplashInfo";
	public static String uploadLeaveInfo = 			index+"Authorization/insertLeaveInfo";
	public static String uploadStudentInfo = 		index+"Authorization/updateUserInfo";
	public static String registerStudentAccount = 	index+"Authorization/idRegister";
	public static String uploadStudentPhoneList = 	index+"Authorization/insertFriendPhone";
	public static String isExistStudentId = 		index+"Authorization/isExistStudentId";
	public static String getStudentFriendInfo = 	index+"Authorization/getFriendInfo";
	public static String getFinishDate = 			index + "Authorization/getFinishDate";
	public static String getNearUserInfo = 			index + "Authorization/getNearUserInfo";
	public static String getStudentCouponAndFinishDate = index + "Authorization/getStudentCouponAndFinishDate";
	public static String getSchoolList =  			index + "Authorization/getSchoolList";
	public static String insertSchoolData = 		index + "Authorization/insertSchoolData";
	public static String isLeaveStudentId =  		index + "Authorization/isLeaveStudentId";
	public static String getFriendInfo		=		index + "Authorization/getFriendInfo";
	//public static String wordsDownload		=		"http://52.79.87.3/admin/index.php/WorkBook/wordsDownload";
	public static String wordsDownload		=		"http://52.79.87.3/admin/index.php/WorkBook/wordsDownload";



	public static String uploadFirstGoalTime = 		index + "StudyInfo/insertStudentGoalTime";
	public static String getWillCoachMent = 		index + "StudyInfo/getWillCoachMent";
	public static String uploadWordLog = 			index + "StudyInfo/insertWordLog";
	public static String uploadClickNextWordSet = 	index + "StudyInfo/updateClickNextWordSet";
	public static String getDailyAchievement = 		index + "StudyInfo/getDailyAchievement";
	public static String insertStudentCalendarData = index+ "StudyInfo/insertStudentCalendarData";
	public static String insertStateZeroWord = 		index+ "StudyInfo/insertStateZeroWord";
	public static String insertStudentForgettingCurves = index + "StudyInfo/insertStudentForgettingCurves";
	public static String getHitCoachMent	= 		index + "StudyInfo/getHitCoachMent";
	public static String getWorkbookList	= 		index + "StudyInfo/getWorkbookList";


	public static String getInviteFreePeriod = 		index + "Payment/getSetInviteFreePeriod";
	public static String insertFinishDateAtLeft = 	index + "Payment/insertFinishDateAtLeft";
	public static String updateFinishDateWithInvite = index + "Payment/updateFinishDateWithInvite";
	public static String getAvailableProductList = 	index + "Payment/getAvailableProductList";
	public static String getProductInfoList  = 		index + "Payment/getProductInfoList";
	public static String getSetDefaultFreePeriod = 	index + "Payment/getSetDefaultFreePeriod";
	public static String insertPaymentRequest = 	index + "Payment/insertPaymentRequest";
	public static String getUsableCoupon = 			index + "Payment/getUsableCoupon";
	public static String insertCoupon = 			index + "Payment/insertCoupon";
	public static String insertPaymentInfo = 		index + "Payment/insertPaymentInfo";
	public static String updatePaymentRequest = 	index + "Payment/updatePaymentRequest";
	public static String insertGoogleItemConsume = 	index + "Payment/insertGoogleItemConsume";


	public static String getBetaServiceAdmin = 		index + "UseAppInfomation/getBetaServiceAdmin";
	public static String insertApplyBetaService = 	index + "UseAppInfomation/insertApplyBetaService";
	public static String insertUserLockScreenInfo = index + "UseAppInfomation/insertUseLockScreenInfo";
	public static String getPolicy = 				index + "UseAppInfomation/getPolicyDocument";
	public static String getUsercount = 			index + "UseAppInfomation/getUserCount";
	public static String updateUserPushLog = 		index + "UseAppInfomation/updateUserPushLog";
	public static String updateTutorialFinished = 	index + "UseAppInfomation/updateTutorialFinished";
	public static String insertWordReport = 		index + "UseAppInfomation/insertWordReport";
	public static String isApplyBetaService = 		index + "UseAppInfomation/isApplyBetaService";
	public static String getUserRestoreDb = 		index + "UseAppInfomation/getUserRestoreDb";
	public static String getBetaServiceAddress = 		index + "UseAppInfomation/getBetaServiceAddress";

	public static String getNoticeBoard = 	index + "Board/getNoticeBoard";
	public static String getFAQBoard = 		index + "Board/getFAQBoard";
	public static String getQnABoard = 		index + "Board/getQnABoard";
	public static String insertQnABoard = 	index + "Board/insertQnABoard";



	public static String insertWordBookEachCount = 	index + "Manager/insertWordBookEachCount";
	public static String getMyExistTest = 			index + "Manager/getMyExistTest";
	public static String insertStudentRelationCode = index + "Manager/insertStudentRelationCode";
	public static String updateFailMakeExam = 		index + "Manager/updateFailMakeExam";
	public static String updateStopTest = 			index + "Manager/updateStopTest";
	public static String insertTestExam = 			index + "Manager/insertTestExam";
	public static String updateFinishTestPaper= 	index + "Manager/updateFinishTestPaper";
	public static String getWordTestResult = 		index + "Manager/getWordTestResult";


	public static String getWorkbookListByWord = 	index + "Word/getWorkbookListByWord";















}
