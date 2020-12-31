/**
 * 
 */
package ac.technion.schemamatching.matchers.firstline;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author David Agronov,Alex Makle,Shoran Yona
 * - class Generic Model
 * Generates genrice model given N schemas using cliques algorthem & minimal cover
 */
public class GenericModel implements FirstLineMatcher {

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getName()
	 */
	@Override
	public String getName() {
		return "GenericModel";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#hasBinary()
	 */
	@Override
	public boolean hasBinary() {
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#match(ac.technion.iem.ontobuilder.core.ontology.Ontology, ac.technion.iem.ontobuilder.core.ontology.Ontology, boolean)
	 */

	//returns most common term in dictionary give proportion
	String[] Web_to_array(String s,String val,double prob)
	{
		Vector<String>terms = new Vector<String>();
		Vector<Integer>value=new Vector<Integer>();
		int flag=0,open=0;
		String attribute_name="";
		String attribute_value="";
		for (int i=0,n=s.length();i<n;i++)
		{
			if(s.charAt(i)=='~')
			{
				if(open==0)
				{
					open=1;
					continue ;
				}
				else
					open=0;
			}
			if(open==1)
			{
				if(s.charAt(i)!='~')
					attribute_name+=s.charAt(i);

			}
			if(open==0)
			{
				if(Character.isWhitespace(s.charAt(i)))
				{
					attribute_name="";
					continue;
				}
				else
				{
					terms.addElement(attribute_name);
					attribute_name="";
					continue;
				}
			}

		}
		double d=(terms.size()*prob);
		int x=(int)d;

		String[] arr=new String[x];
		for (int i=0;i<x;i++)
		{
			arr[i]=terms.get(i);
		}
		return arr;
	}

	public String[] return_model(double proportion)
	{
		String all_attributes="City,64	COUNTRY,61	State,53	Score,49	Testdate,47	email,43	date,37	NAME,36	Title,36	Attachment,30	employer,29	Firstname,28	Lastname,28	street,28	Postalcode,26	TOEFL,24	zip,24	Reading,23	Writing,23	POSITION,22	Totalscore,22	Gender,21	PERMANENTADDRESS,21	TESTSCORES,21	enddate,19	LANGUAGE,19	startdate,19	VerbalScore,19	ValidUntil,18	AcademicInformation,17	GPA,17	IELTS,17	OTHERINFORMATION,17	Subject,17	AdditionalInformation,16	Address,16	Citizenship,16	FIRST_NAME,16	LAST_NAME,16	MIDDLE_NAME,16	PersonalInformation,16	QuantitativeScore,16	Type,16	BIRTHINFORMATION,15	degree,15	Endingdate,15	GRE,15	Startingdate,15	Emailaddress,14	GMAT,14	Listening,14	Middlename,14	Suffix,14	cob,13	Dateofbirth,13	phone,13	Speaking,13	ssn,13	Addressline,12	ceeb_code,12	ETSRegistrationNumber,12	Major,12	Prefix,12	StatementOfPurpose,12	fname,11	lname,11	PUBLICATIONS,11	BIRTH_DATE,10	degreeDate,10	dob,10	Exam_ExamDate,10	HISPANIC,10	InstitutionCode,10	ListAllText,10	NAME_FIRST,10	NAME_LAST,10	NAME_MIDDLE,10	Phoneatthislocation,10	SS_NUM,10	StreetAddress,10	TOEFL_SCORE,10	Upload,10	Verbal,10	alive,9	college,9	CurrentAddress,9	date_deceased,9	degree_received,9	DegreeType,9	DEMOGRAPHICINFORMATION,9	Journal,9	OTHERSCHOOLS,9	parent_address,9	parent_type,9	perferred_email,9	phone_type,9	race,9	RaceEthnicity,9	RecommenderInformation,9	SEX,9	telephone,9	test,9	type_occupation,9	year_received,9	AcademicHistory,8	Award_Institution,8	Award_Year,8	Awarded,8	BIRTH_CITY,8	COLLEGE_BEGIN,8	COLLEGE_CITY,8	COLLEGE_GPA,8	COLLEGE_NAME,8	COLLEGE_STATE,8	CountryOfBirth,8	CountryOfCitizenship,8	cumulativeGPA,8	financialAid,8	greScores,8	InternationalPhoneNumber,8	LANGUAGES,8	LSAT,8	MAIL_CITY,8	MAIL_STREET,8	MAIL_ZIP,8	MCAT,8	permPhone,8	Resume,8	Transcript,8	TranscriptUpload,8	WORK_BEGIN,8	WORK_END,8	AreaofSpecialization,7	education,7	EnrollmentStatus,7	Fellowship,7	FINANCIALINFORMATION,7	GPAscale,7	honors,7	MailingAddress,7	mname,7	NativeLanguage,7	ProgramInformation,7	WaiveAccess,7	ACTIVITIES,6	ANALYTICAL,6	AttendanceLevel,6	BIRTH_LOC_STATE,6	Cellphonenumber,6	CertificationOfAccuracy,6	CITIZEN_US,6	CitizenshipInformation,6	CRIMINAL,6	CRIMINAL_TEXT,6	Exam_Composite,6	Exam_Listening,6	Exam_Reading,6	FOREIGN_STATE,6	FromToDate,6	grade_level,6	GRE_GEN_ANALYTICAL_SCORE,6	GRE_GEN_QUANT_SCORE,6	GRE_GEN_VERBAL_SCORE,6	GRE_SUBJECT_SCORE,6	greDate,6	HighestLevelOfEducation,6	MAIL_DATES_END,6	MAIL_FOREIGN_STATE,6	MajorField,6	math,6	mathdate,6	MGRCS-APP_TERM,6	MGRCS-BIRTH_COUNTRY,6	MGRCS-CANE_ID_NUMBER,6	MGRCS-CELL_CTRY_CODE,6	MGRCS-CELL_PHONE,6	MGRCS-CITIZEN_COUNTRY,6	MGRCS-COLLEGE_DEGREE,6	MGRCS-COLLEGE_DEGREE_DATE,6	MGRCS-COLLEGE_DEGREE_OTHER,6	MGRCS-COLLEGE_MAJOR,6	MGRCS-CONCENTRATION,6	MGRCS-COUNTRY,6	MGRCS-COURSE_CODE,6	MGRCS-COURSE_CREDIT_NUM,6	MGRCS-COURSE_TITLE,6	MGRCS-CTRY_CODE,6	MGRCS-DEPT,6	MGRCS-ENROLL_SCHEDULE,6	MGRCS-ENROLL_STATUS,6	MGRCS-ETHNIC,6	MGRCS-EVE_CTRY_CODE,6	MGRCS-EVE_PHONE,6	MGRCS-EVER_ATTEND,6	MGRCS-FAX,6	MGRCS-FAX_CTRY_CODE,6	MGRCS-GRE_DATE,6	MGRCS-GRE_SUBJECT_DATE,6	MGRCS-GRE_SUBJECT_SUB,6	MGRCS-HISPANIC_ETHNICITY,6	MGRCS-HOW_HEARD,6	MGRCS-HOW_HEARD_OTHER,6	MGRCS-LITERATE_LANGUAGE,6	MGRCS-MAIL_COUNTRY,6	MGRCS-MIAMI_EMPLOYEE,6	MGRCS-NAME_TITLE,6	MGRCS-NATIVE_LANGUAGE,6	MGRCS-OTHER_EVER_ATTEND,6	MGRCS-OTHER_TITLE,6	MGRCS-OTHER_WHEN_ATTEND,6	MGRCS-PASS_NUM,6	MGRCS-PHONE,6	MGRCS-PLEDGE,6	MGRCS-PLEDGE_DATE,6	MGRCS-REC_EMAIL,6	MGRCS-REC_INSTITUTION,6	MGRCS-REC_NAME_FIRST,6	MGRCS-REC_NAME_LAST,6	MGRCS-REC_WAIVE,6	MGRCS-STATEMENT_ALL_HISTORY,6	MGRCS-STATEMENT_ANY_CITATIONS,6	MGRCS-STATEMENT_COMPUTER_SKILLS,6	MGRCS-STUDENT_NAME_BEFORE,6	MGRCS-STUDENT_NUM,6	MGRCS-TOEFL_DATE,6	MGRCS-VISA_TYPE,6	MGRCS-WHEN_ATTEND,6	NAME_ALIAS,6	NAME_COLLEGE_COUNTR,6	Non-USandNon-Canadiantelephonenumber,6	OfficialName,6	OtherUniversities,6	Payment,6	PCITY,6	PERM,6	Pphone,6	PrioiApplication,6	PriorAttendance,6	PZIP,6	readingdate,6	schoolName,6	SUBJECT_SCORE,6	TelephoneFaxNumber,6	TestInformation,6	toeflDate,6	Total,6	USandCanadaTelephoneNumber,6	VISA,6	WORK_EMPLOY,6	WORK_JOB,6	writingdate,6	WritingSample,6	AcademicAveragescore,5	academics,5	Amount,5	Amountoffamilyorparentalsupportexpected,5	AnalyticalWritingAssessmentscore,5	BiologicalSciencesscore,5	Biologyscore,5	classrank,5	CONVICTION_CRIME_VIOLATION,5	current_address,5	CVUpload,5	DAT,5	DateofPublication,5	demographics,5	Department,5	Description,5	EducationalBackground,5	EducationalLoanDebt,5	EDUCATIONHISTORY,5	E-mailaddress,5	EMPLOYMENTHISTORY,5	FacebookPage,5	FacultyMember,5	Fax,5	Federaleducationalloans,5	FIRMORORGANIZATION,5	FundingSource,5	GeneralChemistryscore,5	GPAScoringType,5	GradePointAverage,5	GREORIGINAL,5	GRESUBJECT,5	HomeDebt,5	HomeValue,5	Honors_Prizes_Scholarships,5	Industry,5	INSTITUTION,5	levelofproficiency,5	Majorfieldsofstudy,5	ManageDocuments,5	ManuallyText,5	MaritalStatus,5	Minorfieldsofstudy,5	MISC,5	MOBILEPHONE,5	NOMINATION,5	Number,5	onlinebodiesofwork,5	OrganicChemistryscore,5	OtherSocialMediaOutlets,5	OUTSIDEFUNDING,5	PeceptualAbilityscore,5	permanent_address,5	PERSONALDATA,5	PersonalStatement,5	PhysicalSciencesscore,5	PLACEOFBIRTH,5	Program-basedscholarships,5	PROGRAMOFSTUDY,5	PROGRAMORAREA,5	PROJECTS,5	PublicationTitle,5	PublicationType,5	QuantitativeReasoningscore,5	ReadingComprehensionscore,5	Recommendations,5	RECOMMENDER,5	Recommenders,5	RELATIONSHIPTOYOU,5	RESEARCHAREA,5	ResearchExperience,5	SCHOOL_COUNTRY,5	SEMESTER,5	SKILLSANDINTERESTS,5	Skillsused,5	SkypeID,5	SOCIALMEDIA,5	STANDARDIZEDEXAMS,5	Status,5	Submit,5	Subscore1,5	Subscore2,5	Subscore3,5	TEMPORARYADDRESS,5	Testformat,5	Text,5	TotalSciencescore,5	TSE,5	TwitterFeed,5	TYPEOFDEGREE,5	URL,5	USCITIZEN_YESNO,5	VerbalReasoningscore,5	VeteransBenefits,5	WritingSamplescore,5	WritingSampleUpload,5	WritingScore,5	AbstractURL,4	Academic_EmployerReference_Sponsorship,4	AcademicAndOtherSignificantWorkExperience,4	AcademicInterest,4	AcademicProbation,4	ACADEMICTERM_ID,4	Affiliation,4	AltPhone,4	ANALYTICAL_PER,4	AnalyticalWriting,4	Applicant_CitizenshipCode,4	AREAID,4	Authors,4	background,4	BandScore,4	BCITY,4	BCOUNTRY_ID,4	BiographicalInformation,4	biography,4	BSTATE_ID,4	CAreaPhone,4	CCITY,4	CCOUNTRY_ID,4	CCOUNTRYPHONE,4	CellPhone,4	CENROLL,4	certification_info,4	CITIZENSHIP_ID,4	CitizenshipStatus,4	cityob,4	cityzenship,4	College_University,4	competition,4	Concentration,4	ContactInformation,4	Cphone,4	CriminalOffense,4	CSTATE_ID,4	CSTREET,4	CurrentContactInformation,4	currentMailAddress,4	currentMailCity,4	currentMailCountry,4	CurrentMailingAddress,4	currentMailState,4	currentMailStreet,4	currentMailZip,4	currentPhone,4	currResUntil,4	CZIP,4	dateEntered,4	dateLeft,4	DayTelephone,4	DegreeAwarded,4	DEGREEID,4	degreeProgram,4	Departmental,4	Diversity,4	DNSC_106_date_results_requested,4	DNSC_107_date_results_requested,4	DNSC_109_date_results_requested,4	DNSC_1103_dynamic_short_varchar,4	DNSC_1127_dynamic_one_char,4	DNSC_1128_dynamic_one_char,4	DNSC_1129_dynamic_one_char,4	DNSC_1137_dynamic_long_varchar,4	DNSC_1138_dynamic_long_varchar,4	DNSC_1139_dynamic_long_varchar,4	DNSC_1140_dynamic_one_char,4	DNSC_1158_dynamic_integer,4	DNSC_1238_dynamic_short_varchar,4	DNSC_1406_dynamic_short_varchar,4	DNSC_157_course_title,4	DNSC_2491_dynamic_long_varchar,4	DNSC_2512_dynamic_long_varchar,4	DNSC_2558_dynamic_two_char,4	DNSC_32_address_city,4	DNSC_32_address_country,4	DNSC_32_address_intl_code,4	DNSC_32_address_state,4	DNSC_32_address_street,4	DNSC_32_address_zip,4	DNSC_35_first_name,4	DNSC_35_last_name,4	DNSC_51_first_name,4	DNSC_51_last_name,4	DNSC_555_dynamic_one_char,4	DNSC_601_major,4	DNSC_602_major,4	DNSC_65_college_degree_date,4	DNSC_65_college_degree_earned,4	DNSC_65_college_from,4	DNSC_65_college_major,4	DNSC_65_college_name,4	DNSC_65_college_to,4	DNSC_711_dynamic_one_char,4	DNSC_712_dynamic_one_char,4	DNSC_728_dynamic_text,4	DNSC_75593_dynamic_long_varchar_option,4	DNSC_767_dynamic_smalldatetime,4	Document,4	DSC_1_address_city,4	DSC_1_address_country,4	DSC_1_address_intl_code,4	DSC_1_address_state,4	DSC_1_address_street,4	DSC_1_address_zip,4	DSC_106_integer_score1,4	DSC_106_integer_score2,4	DSC_106_integer_score3,4	DSC_106_integer_score4,4	DSC_106_test_date,4	DSC_1076_dynamic_short_varchar,4	DSC_109_integer_score1,4	DSC_109_integer_score2,4	DSC_109_integer_score3,4	DSC_109_integer_score4,4	DSC_109_test_date,4	DSC_1092_address_city,4	DSC_1092_address_country,4	DSC_1092_address_state,4	DSC_1335_phone_number,4	DSC_15320_dynamic_long_varchar,4	DSC_15321_dynamic_long_varchar,4	DSC_16248_phone_number,4	DSC_2_address_city,4	DSC_2_address_country,4	DSC_2_address_intl_code,4	DSC_2_address_state,4	DSC_2_address_street,4	DSC_2_address_zip,4	DSC_2494_dynamic_long_varchar_option,4	DSC_2495_dynamic_long_varchar,4	DSC_2706_dynamic_long_varchar,4	DSC_3_first_name,4	DSC_3_last_name,4	DSC_3_middle_name,4	DSC_4_first_name,4	DSC_4_last_name,4	DSC_4_middle_name,4	DSC_52_phone_number,4	DSC_54_phone_number,4	DSC_56_phone_number,4	DSC_561_activity_date_from,4	DSC_561_activity_date_to,4	DSC_561_activity_org_or_company,4	DSC_848_phone_number,4	DSC_937_dynamic_one_char,4	Edu_Degree,4	EDU_INST_CODE,4	EducationalInstitutions,4	Effdate,4	Employers,4	EmploymentExperience,4	EmploymentInformation,4	EnglishProficiencyScores,4	EnrollmentInformation,4	EnrollmentPlans,4	enrollType,4	EssayWritingCBT,4	EthnicityandRace,4	EveningTelephone,4	Exam_Essay,4	Exam_RegNum,4	Exam_Structure,4	FacultyInterest,4	fee_type,4	FELLOWSHIP_TYPE,4	Fellowships,4	FINANCIAL_AWARD,4	FinancialAidPreference,4	ForeignState,4	FormerName,4	funding_school,4	gendeRaceDeclaration,4	GeneralTests,4	GPA_C,4	GPA_Major,4	GPA_Scale,4	GPAScore,4	greA,4	GREG,4	greQ,4	GRES,4	GRESubjectTestScore,4	greV,4	GREW,4	his_race,4	HONOR_FILE,4	HONOR_ID,4	HonorsAndAwards,4	imm_stat,4	Institute,4	InstitutionName,4	IRT,4	is_latino,4	lang,4	LanguageAbilities,4	LANGUAGEID,4	Location,4	MAIDEN,4	majorSubject,4	McNairScholar,4	MI,4	NATIONALITYID,4	NUMAPPLIED,4	ONAME,4	OtherBackground,4	OtherPersonalInformation,4	OtherUniversitiesAppliedTo,4	Pages,4	PAreaPhone,4	PCOUNTRY_ID,4	PCOUNTRYPHONE,4	PermanentContactInformation,4	PermanentHomeCountryAddress,4	PermanentMailingAddress,4	permEmail,4	permFax,4	permMailCity,4	permMailCountry,4	permMailState,4	permMailStreet,4	permMailZip,4	PlaceofLegalResidence,4	PlacofBirth,4	PlansForGraduateStudy,4	PreferredFacultyAdvisor,4	PreferredName,4	PREV_INST_CODE,4	ProfessionalMisConduct,4	Proficiency,4	PSTATE_ID,4	PSTREET,4	PSTREET2,4	PublicationOrPresentationOfOriginalWork,4	Published_Date,4	Pzip2,4	QUANT,4	QUANT_PER,4	Quantitative,4	READ_ABI,4	References_Sponsors,4	REG_CODE,4	RegistrationNumber,4	Relationship,4	SampleOfWork,4	School_GradDate,4	School_SchoolName,4	SNSC_295_enroll_term_year_code,4	SocialSecurityNumber,4	SPEAK_ABI,4	SpeakingIBT,4	SSC_133_email,4	SSC_142_dob,4	SSC_144_state_of_birth,4	SSC_145_country_of_birth,4	SSC_146_gender,4	SSC_212_state_of_current_residence,4	SSC_218_country_of_residence,4	SSC_285_ssn,4	SSC_339_city_of_birth,4	StateCode,4	StudentName,4	StudyAbroad,4	SUBJECT_AREA,4	SUBJECT_SCORE_PER,4	SubjectTests,4	SUBSCORE_NAME,4	SUBSCORE_SCORE,4	SummerResearch,4	SupplementaryApplicationForm,4	Survey,4	TeachingExperience,4	Term,4	TestingInformation,4	TestMonth,4	TestOfWrittenEnglishPBT,4	TestYear,4	toefl_ibt_l,4	toefl_ibt_r,4	toefl_ibt_s,4	toefl_ibt_w,4	toefl_total,4	toefl_type,4	toeflScores,4	TRFNumber,4	UConnReferer,4	University,4	UNIVERSITY_CODE,4	UnofficialTranscripts,4	USSocialSecurityNumber,4	VERBAL_PER,4	VISAID,4	VisaType,4	Volume,4	Work_Nature,4	workcompany,4	workExperience,4	workstart,4	workstop,4	worktitle,4	WRITE_ABI,4	WRITING_SCORE,4	sat,3	AcademicProgram,3	act,3	AdditionalInstitution,3	AffiliatewithInterdisciplinaryCluster,3	age,3	AnalyticalScore,3	AnalyticalWritingScore,3	ap_ib_sat_subject,3	applicant,3	attendance_date,3	AttendanceStatus,3	Award,3	BackgroundInformation,3	BioscienceScore,3	birthdate,3	ceeb,3	cell_phone,3	CICCode,3	CICScholar,3	citizenship_status,3	CitizenStatus,3	city_of_birth,3	colleges,3	composite,3	compositedate,3	CompositeScore,3	counselor_email,3	counselor_fax,3	counselor_fname,3	counselor_lname,3	counselor_mname,3	counselor_phone,3	counselor_prefix,3	counselor_title,3	country_of_birth,3	CrimeConviction,3	current_year_courses,3	CurrentTelephoneNumber,3	date_entry,3	DisciplinaryViolation,3	DiversityStatement,3	DPT-PhD,3	education_interruption,3	english,3	englishdate,3	EntryQuarter,3	Explanation,3	FacultyMembers,3	family,3	FieldOfStudy,3	first_language,3	first_semester,3	FirstRaisingIndividual,3	Fluency,3	GPAweighting,3	grades,3	GraduateRecordExaminationGeneralTest,3	GraduateRecordExaminationSubjectTest,3	graduatingclass,3	home_phone,3	honor,3	HonorsAchievement,3	household,3	im_address,3	im_type,3	InfluenceFactorsOfApplyDecision,3	inter-national,3	isenrolled,3	islatino,3	JD-PhD,3	JuniorGPA,3	LawDegree,3	legal_guardian,3	ListeningBandScore,3	marital_status,3	MaximumGPA,3	MLAFormat,3	national,3	num_children,3	organization,3	OverallBandScore,3	parent1,3	parent2,3	perferred_name,3	perferred_telephone,3	permanent_home,3	PermanentTelephoneNumber,3	personal_data,3	PhysicalScienceScore,3	PhysicalTherapyDegree,3	PreviouslyApplication,3	ProfessionalPlans,3	read,3	ReadingBandScore,3	religious_preference,3	Scale,3	school,3	school_address,3	school_attended,3	school_city,3	school_state,3	school_type,3	school_zip,3	science,3	sciencedate,3	second_semester,3	secondary_schools,3	SecondRaisingIndividual,3	SeniorGPA,3	siblings,3	Signature,3	speak,3	SpeakingBandScore,3	spoken_at_home,3	state_of_birth,3	TestingDate,3	TestType,3	third_semester,3	Undergraduate,3	UndergraduateInstitution,3	UploadAttachment,3	VEFCode,3	VEFFellowship,3	write,3	WritingBandScore,3	years_lived_inside_US,3	years_lived_outside_US,3	AdditionalEducationHistory,2	AdditionalProgramInformation,2	AdditionalQuestions,2	ADDR_VALID_TO_DATE,2	Address_Information,2	AdmissionInformation,2	Adv_GRE_Subject,2	advance_gpa,2	ADVGREdate,2	ALIEN_NUMBER,2	ALT_CONTACT_ADDR,2	ALT_CONTACT_NAME,2	analytical_percent,2	app_term,2	APPLCNT_CURR_INCOME_AMT,2	APPLCNT_CURR_OCCUP_TYPE,2	Applicant_BirthCity,2	Applicant_BirthCountry,2	Applicant_BirthStateCode,2	Applicant_CelPhone,2	Applicant_CitCountry,2	Applicant_Dob,2	Applicant_Email,2	Applicant_Email2,2	Applicant_FirstName,2	Applicant_Gender,2	Applicant_LastName,2	Applicant_MiddleName,2	Applicant_NickName,2	Applicant_Ssn,2	Applicant_Title,2	Applicant_VisaCode,2	Applicantion_PreviousFirstName,2	Applicantion_PreviousLastName,2	Application_PreviousApplYear,2	area,2	ATTEND_FROM_DATE,2	ATTEND_TO_DATE,2	AttendancesDate,2	Award_AwardDesc,2	Award_Program,2	Awards,2	BACHELOR_DEG_SCHOOL_FLAG,2	banner,2	basiscd,2	BiliographicalInformation,2	BIRTH_COUNTRY,2	birthCity,2	birthCountry,2	BIRTHPLACE_CITY,2	BIRTHPLACE_CNTRY_NAME,2	BIRTHPLACE_STATE,2	birthState,2	CA_RESID_EXPECT_FLAG,2	CA_RESID_START_DATE,2	campus_id,2	cBT,2	CELL_TEL_NUMBER,2	ChinaScholarshipCouncilProgram,2	Citinzenship,2	citizen,2	CITIZEN_COUNTRY,2	citizenCountry,2	CITZN_CNTRY_CD,2	COLL_GRAD_NAME,2	COLLEGE_COUNTRY,2	COLLEGE_DEGREE,2	COLLEGE_DEGREE_DATE,2	COLLEGE_END,2	COLLEGE_GPA_SCALE,2	COLLEGE_MAJOR,2	CORNELLG-ACADEMIC_HONORS,2	CORNELLG-ADDRESS_VALID_DATE,2	CORNELLG-ALTERNATE_FIELD,2	CORNELLG-APPLY_EMPLOYEE_PROGRAM_YN,2	CORNELLG-APPLYING_OTHER,2	CORNELLG-CBT_DATE,2	CORNELLG-CBT_SCORE.1,2	CORNELLG-CBT_SCORE.2,2	CORNELLG-CBT_SCORE.3,2	CORNELLG-CBT_SCORE_TOTAL,2	CORNELLG-CELL_PHONE,2	CORNELLG-CELL_PHONE_AREA,2	CORNELLG-CITIZENSHIP,2	CORNELLG-COLLEGE_DEGREE_TYPE,2	CORNELLG-COLLEGE_DEGREE_YN,2	CORNELLG-CONCENTRATION,2	CORNELLG-CURRENT_VISA,2	CORNELLG-CURRENTLY_ENROLLED_YN,2	CORNELLG-DEP_BIRTH_CITY,2	CORNELLG-DEP_BIRTH_COUNTRY,2	CORNELLG-DEP_BIRTH_DATE,2	CORNELLG-DEP_NAME_FIRST,2	CORNELLG-DEP_NAME_LAST,2	CORNELLG-DEP_RELATIONSHIP,2	CORNELLG-EMPLOYMENT_BEGIN_DATE,2	CORNELLG-EMPLOYMENT_DETAILS,2	CORNELLG-EMPLOYMENT_END_DATE,2	CORNELLG-EMPLOYMENT_INSTITUTION,2	CORNELLG-EMPLOYMENT_POSITION,2	CORNELLG-ENROLLED_UNDERGRAD_TERM,2	CORNELLG-ENROLLED_UNDERGRAD_YN,2	CORNELLG-EXPECTED_VISA,2	CORNELLG-FACULTY_CONSULTED,2	CORNELLG-FELLOWSHIPS_YN,2	CORNELLG-FIN_SUPPORT_AMOUNT,2	CORNELLG-FIN_SUPPORT_DATE,2	CORNELLG-FIN_SUPPORT_SOURCE,2	CORNELLG-FIN_SUPPORT_STATUS,2	CORNELLG-GENDER,2	CORNELLG-GMAT_DATE,2	CORNELLG-GMAT_SCORE_AWA,2	CORNELLG-GMAT_SCORE_QUANT,2	CORNELLG-GMAT_SCORE_VERBAL,2	CORNELLG-GPA_MAJOR,2	CORNELLG-GPA_MAJOR_MAXIMUM,2	CORNELLG-GPA_MAXIMUM,2	CORNELLG-GPA_OVERALL,2	CORNELLG-GRE_DATE,2	CORNELLG-GRE_REG_NUMBER,2	CORNELLG-GRE_SCORE_ANALYTICAL,2	CORNELLG-GRE_SCORE_QUANT,2	CORNELLG-GRE_SCORE_VERBAL,2	CORNELLG-GRE_SUB,2	CORNELLG-GRE_SUB_DATE,2	CORNELLG-GRE_SUB_REG_NUMBER,2	CORNELLG-GRE_SUB_SCORE,2	CORNELLG-IBT_DATE,2	CORNELLG-IBT_SCORE_LISTENING,2	CORNELLG-IBT_SCORE_READING,2	CORNELLG-IBT_SCORE_SPEAKING,2	CORNELLG-IBT_SCORE_TOTAL,2	CORNELLG-IBT_SCORE_WRITING,2	CORNELLG-INTL_CITY,2	CORNELLG-INTL_COUNTRY,2	CORNELLG-INTL_PHONE,2	CORNELLG-INTL_PHONE_AREA,2	CORNELLG-INTL_POSTAL,2	CORNELLG-INTL_STATE,2	CORNELLG-INTL_STREET,2	CORNELLG-LANGUAGES,2	CORNELLG-NAME_SUFFIX,2	CORNELLG-NATIVE_LANGUAGE,2	CORNELLG-OTHER_NAME_FIRST,2	CORNELLG-OTHER_NAME_LAST,2	CORNELLG-OTHER_NAME_MIDDLE,2	CORNELLG-PERMANENT_RES_YN,2	CORNELLG-PHONE,2	CORNELLG-PHONE_AREA,2	CORNELLG-PLEDGE_DATE,2	CORNELLG-PLEDGE_YN,2	CORNELLG-PREVIOUSLY_ATTENDED_END,2	CORNELLG-PREVIOUSLY_ATTENDED_START,2	CORNELLG-PREVIOUSLY_ATTENDED_YN,2	CORNELLG-PUBLICATIONS,2	CORNELLG-SMS_PHONE,2	CORNELLG-SMS_PHONE_AREA,2	CORNELLG-SPECIALTY,2	CORNELLG-TOEFL_DATE,2	CORNELLG-TOEFL_ID,2	CORNELLG-TOEFL_SCORE,2	CORNELLG-TOEFL_TYPE,2	coursename,2	Courses,2	courseunits,2	cum_gpa,2	Current,2	DateEarnedOrExpected,2	DateTaken,2	DEG_AWD_DATE,2	DEG_AWD_TYPE,2	DEPEND_ADULT_TOT,2	DEPEND_CHILD_LIVIN_FLAG,2	DEPEND_CHILD_TOT,2	Dependents,2	disability,2	DomesticFellowship,2	Educational_Background,2	E-mail,2	Empl_City,2	Empl_Country,2	Empl_Employer,2	Empl_EmploymentEnd,2	Empl_EmploymentStart,2	Empl_EmploymentType,2	Empl_ForeignState,2	Empl_StateCode,2	Empl_Supervisor,2	empname,2	emptitle,2	ethnic,2	ETHNIC_AM_INDIAN_TRIBE_NAME,2	ETHNIC_CD,2	ethnicity,2	EthnicityCode,2	EthnicityInformation,2	Exam_AnalyticalPercent,2	Exam_AnalyticalScore,2	Exam_Ppi,2	Exam_QuantitativePercent,2	Exam_QuantitativeScore,2	Exam_VerbalPercent,2	Exam_VerbalScore,2	Exam_Writing,2	ExamRequirements,2	ExamSubject,2	ExternalFinancialAward,2	facname,2	facreason,2	FacultyPreference,2	FATHER_EDUC_LEVEL,2	FIELD_OF_STUDY_TEXT,2	FifthYearM.S.Application,2	fifthyra,2	fifthyrb,2	fifthyrc,2	FinancialResource,2	FinancialSupportApplication,2	FLASFellowship,2	FLWSHP_FLAS_ESSAY_TEXT,2	FLWSHP_FLAS_FOR_LANG_LEVEL_CD,2	FLWSHP_FLAS_FOR_LANG_NAME,2	FLWSHP_FLAS_WRLD_AREA_CD,2	Funding,2	funding_prog,2	funding_yr,2	grad_gpa,2	GRE_Subject,2	greanalyticpercent,2	greanalyticscore,2	greexamdate,2	grequantpercent,2	grequantscore,2	gresubexamdate,2	gresubjectscore,2	gresubpercent,2	greverbpercent,2	greverbscore,2	GSPUAPPL-APP_TERM,2	GSPUAPPL-APPLY_BEFORE,2	GSPUAPPL-APPLY_BEFORE_DEPT,2	GSPUAPPL-APPLY_BEFORE_YEAR,2	GSPUAPPL-ATTEND_HS_NJ,2	GSPUAPPL-ATTEND_HS_NJ_NAME,2	GSPUAPPL-BIRTH_CITY,2	GSPUAPPL-BIRTH_COUNTRY,2	GSPUAPPL-BIRTH_STATE,2	GSPUAPPL-BIRTH_STATE_INTL,2	GSPUAPPL-CELL_PHONE_AREA,2	GSPUAPPL-CELL_PHONE_PRE,2	GSPUAPPL-CELL_PHONE_SUF,2	GSPUAPPL-CITIZEN_COUNTRY,2	GSPUAPPL-CITIZENSHIP_STATUS,2	GSPUAPPL-COLLEGE_BEGIN,2	GSPUAPPL-COLLEGE_DEGREE,2	GSPUAPPL-COLLEGE_DEGREE_DATE,2	GSPUAPPL-COLLEGE_END,2	GSPUAPPL-COLLEGE_GPA,2	GSPUAPPL-COLLEGE_MAJOR,2	GSPUAPPL-COLLEGE_MAJOR_GPA,2	GSPUAPPL-COLLEGE_MAJOR2,2	GSPUAPPL-COLLEGE_MAJOR2_GPA,2	GSPUAPPL-COLLEGE_MINOR,2	GSPUAPPL-COLLEGE_MINOR2,2	GSPUAPPL-COLLEGE_NAME,2	GSPUAPPL-COUNTRY,2	GSPUAPPL-COUNTRY_RESIDENCE,2	GSPUAPPL-DEGREE_DESCRIPTION,2	GSPUAPPL-DEPARTMENT,2	GSPUAPPL-ETHNIC,2	GSPUAPPL-ETHNIC_HISPANIC_N,2	GSPUAPPL-ETHNIC_TRIBAL_AFFILIATION,2	GSPUAPPL-ETHNIC_TRIBAL_DATE_ENROLLED,2	GSPUAPPL-EXTERNAL_FELLOWSHIP,2	GSPUAPPL-EXTERNAL_FELLOWSHIP_TEXT,2	GSPUAPPL-FINANCE_APPLICANT_BUSINESS,2	GSPUAPPL-FINANCE_APPLICANT_CASH,2	GSPUAPPL-FINANCE_APPLICANT_HOME,2	GSPUAPPL-FINANCE_APPLICANT_INCOME,2	GSPUAPPL-FINANCE_APPLICANT_INVEST,2	GSPUAPPL-FINANCE_APPLICANT_REAL_EST,2	GSPUAPPL-FINANCE_APPLYING_FOR_SUPPORT,2	GSPUAPPL-FINANCE_CHILDREN_AGE,2	GSPUAPPL-FINANCE_CHILDREN_HAVE,2	GSPUAPPL-FINANCE_CHILDREN_NUM,2	GSPUAPPL-FINANCE_DEBT_GRAD,2	GSPUAPPL-FINANCE_DEBT_UNDERGRAD,2	GSPUAPPL-FINANCE_FAMILY_ACCOMPANY,2	GSPUAPPL-FINANCE_FATHER_EMPLOYER,2	GSPUAPPL-FINANCE_FATHER_INCOME,2	GSPUAPPL-FINANCE_FATHER_OCCUPATION,2	GSPUAPPL-FINANCE_INTEND_METHOD,2	GSPUAPPL-FINANCE_MOTHER_EMPLOYER,2	GSPUAPPL-FINANCE_MOTHER_INCOME,2	GSPUAPPL-FINANCE_MOTHER_OCCUPATION,2	GSPUAPPL-FINANCE_OTHER_INCOME,2	GSPUAPPL-FINANCE_PARENT_BUSINESS,2	GSPUAPPL-FINANCE_PARENT_CASH,2	GSPUAPPL-FINANCE_PARENT_HOME,2	GSPUAPPL-FINANCE_PARENT_INVEST,2	GSPUAPPL-FINANCE_PARENT_MARITAL,2	GSPUAPPL-FINANCE_PARENT_REAL_EST,2	GSPUAPPL-FINANCE_PLEDGE,2	GSPUAPPL-FINANCE_PLEDGE_DATE,2	GSPUAPPL-FINANCE_SPOUSE_EMPLOYED,2	GSPUAPPL-FINANCE_SPOUSE_INCOME,2	GSPUAPPL-FINANCE_SPOUSE_OCCUPATION,2	GSPUAPPL-FINANCE_SPOUSE_STUDENT,2	GSPUAPPL-FINANCE_SPOUSE_STUDENT_WHERE,2	GSPUAPPL-FOREIGN_LANG,2	GSPUAPPL-FOREIGN_LANG_ABILITY_READING,2	GSPUAPPL-FOREIGN_LANG_ABILITY_SPEAKING,2	GSPUAPPL-FOREIGN_LANG_ABILITY_WRITING,2	GSPUAPPL-HIGHEST_EDUC_LEVEL,2	GSPUAPPL-HIGHEST_EDUC_LEVEL_DEGREE_YEAR,2	GSPUAPPL-HIGHEST_EDUC_LEVEL_SCH_NAME,2	GSPUAPPL-MAIL_COUNTRY,2	GSPUAPPL-MAIL_STATE,2	GSPUAPPL-MAIL_TEMPLATE_ADDRESS,2	GSPUAPPL-MAIL_TEMPLATE_ADDRESS_EXT,2	GSPUAPPL-MAIL_TEMPLATE_AREA_CODE,2	GSPUAPPL-MAIL_TEMPLATE_CAP,2	GSPUAPPL-MAIL_TEMPLATE_CITY,2	GSPUAPPL-MAIL_TEMPLATE_COMMUNITY,2	GSPUAPPL-MAIL_TEMPLATE_DISTRICT,2	GSPUAPPL-MAIL_TEMPLATE_HOUSE_NO,2	GSPUAPPL-MAIL_TEMPLATE_LOCATION,2	GSPUAPPL-MAIL_TEMPLATE_POST_CODE,2	GSPUAPPL-MAIL_TEMPLATE_POSTAL,2	GSPUAPPL-MAIL_TEMPLATE_POSTAL_CODE,2	GSPUAPPL-MAIL_TEMPLATE_PROVINCE,2	GSPUAPPL-MAIL_TEMPLATE_STATE,2	GSPUAPPL-MAIL_TEMPLATE_STREET,2	GSPUAPPL-MAIL_TEMPLATE_TOK_SOI,2	GSPUAPPL-MAJOR_AREAS_OF_INTEREST,2	GSPUAPPL-MAJOR_DEGREE_CODE,2	GSPUAPPL-MARITAL_STATUS,2	GSPUAPPL-NATIVE_LANGUAGE,2	GSPUAPPL-OTHER_COLLEGE_APPLY_NAME,2	GSPUAPPL-OTHER_COLLEGE_APPLY_OTHER,2	GSPUAPPL-OTHER_NAMES,2	GSPUAPPL-PLEDGE,2	GSPUAPPL-PLEDGE_DATE,2	GSPUAPPL-PUBLICATION_PUBLISHER,2	GSPUAPPL-PUBLICATION_TITLE_TOPIC,2	GSPUAPPL-PUBLICATION_TYPE,2	GSPUAPPL-PUBLICATION_YEAR,2	GSPUAPPL-SKYPE_ADDRESS,2	GSPUAPPL-STATE,2	GSPUAPPL-SUSPEND_TEXT,2	GSPUAPPL-SUSPEND_TEXT_DATE,2	GSPUAPPL-SUSPEND_TEXT_LEARN,2	GSPUAPPL-TEMPLATE_ADDRESS,2	GSPUAPPL-TEMPLATE_ADDRESS_EXT,2	GSPUAPPL-TEMPLATE_AREA_CODE,2	GSPUAPPL-TEMPLATE_CAP,2	GSPUAPPL-TEMPLATE_CITY,2	GSPUAPPL-TEMPLATE_COMMUNITY,2	GSPUAPPL-TEMPLATE_DISTRICT,2	GSPUAPPL-TEMPLATE_HOUSE_NO,2	GSPUAPPL-TEMPLATE_LOCATION,2	GSPUAPPL-TEMPLATE_POST_CODE,2	GSPUAPPL-TEMPLATE_POSTAL,2	GSPUAPPL-TEMPLATE_POSTAL_CODE,2	GSPUAPPL-TEMPLATE_PROVINCE,2	GSPUAPPL-TEMPLATE_STATE,2	GSPUAPPL-TEMPLATE_STREET,2	GSPUAPPL-TEMPLATE_TOK_SOI,2	GSPUAPPL-USCIS_RECEIPT_NUM,2	GSPUAPPL-VISA_TYPE,2	GSPUAPPL-WAIVER_PROGRAM_PARTICIPATION,2	HighSchool,2	HISPANIC_FLAG,2	hispanic_orig,2	HOME_STATE,2	HOME_STATE_MONTH_TOT,2	HOME_STATE_YR_TOT,2	HS_GRAD_NAME,2	iBT,2	IELTSdate,2	ieltsexamdate,2	ieltslisteningscore,2	ieltsoverallscore,2	ieltsreadingscore,2	IELTSscore,2	ieltsspeakingscore,2	ieltswritingscore,2	INFO_RELEASE_AUTH_FLAG,2	Institutions,2	internationalGPA,2	InternationFellowship,2	INTL_CHILD_CARE_EXPNS_AMT,2	INTL_FLWSHP_ENDOW_FUND_CD,2	INTL_KIN_AID_AMT,2	INTL_LOAN_PMT_AMT,2	INTL_LOAN_PMT_TEXT,2	INTL_MED_DENT_EXPNS_AMT,2	INTL_MED_DENT_EXPNS_TEXT,2	INTL_OTHER_EXPNS_AMT,2	INTL_OTHER_EXPNS_TEXT,2	INTL_OTHER_RESRCE_AMT,2	INTL_OTHER_RESRCE_TEXT,2	INTL_PARENT_AID_AMT,2	INTL_PERS_RESRCE_AMT,2	INTL_RELOC_EXPNS_AMT,2	INTL_SPONSOR_AID_AMT,2	irt_program,2	irt_yr,2	Language_Language,2	Language_Other,2	Language_YearsStudiedCollege,2	Language_YearsStudiedHighSchool,2	maddr,2	maddr_effdt_from,2	maddr_effdt_to,2	major_gpa,2	MajorGPA,2	MarcScholar,2	mcity,2	mcnair,2	mcountry,2	mdayphone,2	MELAB,2	MELABdate,2	MELABscore,2	MOTHER_EDUC_LEVEL,2	mphone,2	mstate,2	mzip,2	NameOfSchool,2	NativeLanguageInformation,2	Opportunities,2	OptionalPassportInformation,2	otherapp,2	paddr,2	paddr_effdt_from,2	paddr_effdt_to,2	PARENT_TAX_EXMPT_FLAG,2	parents,2	PaymentMethod,2	pcountry,2	pdayphone,2	people,2	Percent,2	PERM_CITY,2	PERM_COUNTRY,2	PERM_FAX_NUMBER,2	PERM_STATE,2	PERM_STREET,2	PERM_TEL_NUMBER,2	PERM_ZIP_POSTAL_CD,2	Permanent,2	PERSONAL_HISTORY_STATEMENT,2	Personal_Information,2	PersonalFamilyInformation,2	PersonalHistoryStatement,2	PHONE_AREA,2	PHONE_COUNTRY_CODE,2	PHONE_PRE,2	PHONE_SUF,2	Positions,2	preaddr,2	preaddr_effdt_from,2	preaddr_effdt_to,2	precity,2	precountry,2	PreliminaryQuestions,2	prestate,2	PREV_FIRST_NAME,2	PREV_LAST_NAME,2	PREV_MIDDLE_NAME,2	PREV_UCB_GRAD_APPL,2	PREV_UCB_GRAD_APPL_PROGRAM,2	PREV_UCB_GRAD_APPL_TERM_CD,2	PREV_UCB_GRAD_APPL_YR,2	PREV_UCB_STU,2	PREV_UCB_STU_ID,2	Previous_Address,2	PreviousApplication,2	PreviousCollege,2	PreviousEducation,2	prezip,2	PRIM_HOME_LANG_NAME,2	ProfessionalExperience,2	pstate,2	quan,2	quan_percent,2	RaceCode,2	RCMDR_EMAIL,2	RCMDR_FIRST_NAME,2	RCMDR_INSTITUTION,2	RCMDR_LAST_NAME,2	RCMDR_PHONE,2	RCMDR_WAIVE,2	Recom_Country,2	Recom_Dept,2	Recom_Email,2	Recom_FirstName,2	Recom_ForeignState,2	Recom_Institution,2	Recom_LastName,2	Recom_Paper,2	Recom_Phone,2	Recom_RecomRelationshipId,2	Recom_RecomRelationshipOther,2	Recom_StateCode,2	Recom_Title,2	RelevantWorkExperience,2	ResearchAreas,2	RESIDENT_ALIEN_NUM,2	Results,2	School_AttendStart,2	School_Ceeb,2	SCHOOL_CODE,2	school_dt_from,2	school_dt_to,2	SCHOOL_LOC_NAME,2	School_Major,2	School_MajorGpa,2	School_Minor,2	SCHOOL_NAME,2	School_OtherGpa,2	School_OverallGpa,2	School_SchoolCity,2	schoollocation,2	SelfReportedExamScores,2	SIBLING_ATTEND_COLL_TOT,2	SIBLING_CURR_COLL_TOT,2	SIBLING_TOT,2	SignatureStatement,2	SOP_ESSAY,2	Sources,2	SPECIAL_PROGRAMS,2	srop,2	StateOrProvince,2	StudentID,2	StudentOrganizations,2	subject_score_percent,2	subjectname,2	SubmitType,2	SupplInfo_FacultyConsulted,2	SupplInfo_InfoConsulted,2	SupplInfo_LearnAbout_AMF,2	SUSPEND,2	TEMP_CITY,2	TEMP_COUNTRY,2	TEMP_FAX_NUMBER,2	TEMP_STATE,2	TEMP_STREET,2	TEMP_TEL_NUMBER,2	TEMP_ZIP_POSTAL_CD,2	termdegree,2	test_scores,2	TOEFL_CBT,2	TOEFL_IBT,2	TOEFL_PBT,2	toeflexamdate,2	toefllisteningscore,2	toeflreadingscore,2	TOEFLscore,2	toeflspeakingscore,2	toefltotal,2	TOEFLtype,2	toeflwritingscore,2	TRANSCRIPT_SCHOOL,2	trio,2	US_ASSET_AMT,2	US_CHILD_CARE_EXPNS_AMT,2	US_EDUC_FED_LOAN_AMT,2	US_EDUC_LOAN_BAL_AMT,2	US_EDUC_PRIV_LOAN_AMT,2	US_FLWSHP_ENDOW_FUND_CD,2	US_INTEREST_INCOME_AMT,2	US_MED_DENT_EXPNS_AMT,2	US_MED_DENT_EXPNS_TEXT,2	US_OTHER_EXPNS_AMT,2	US_OTHER_EXPNS_TEXT,2	US_OTHER_INCOME_AMT,2	US_OTHER_INCOME_TEXT,2	US_PARENT_AID_AMT,2	US_RELOC_EXPNS_AMT,2	US_STOCK_BOND_AMT,2	US_SUMMER_INCOME_AMT,2	ValidDate,2	verbal_percent,2	veteran,2	VETERAN_YN,2	VISA_CD,2	VisaInformation,2	VOTE_REG_STATE,2	Waiver,2	weblink,2	WebLinks,2	WORK_EMPLOYER,2	WORK_HISTORY,2	WORK_POSITION,2	WorkHistory,2	ZipCodeOrMailCode,2	AcademicTranscripts,1	AdditionalDescription,1	AdvancedDegree,1	AlternateE-mail,1	ApplicantSection,1	AppliedFundingPrograms,1	ApplyingProgram,1	Confirmation,1	CurrentDaytimeTelephone,1	DesiredProgram,1	FieldOfStudyCode,1	FilingStatus,1	GenerationalQualifier,1	GraduatedFamily,1	GREGeneral,1	HonorsOrScholashipsOrFellowships,1	ImmigrationStatus,1	InfluenceIndividuals,1	LanguageAbility,1	LanguageOfInstruction,1	LanguageSpokenAtHome,1	Nationality,1	Origin,1	OTHERACADEMICINITIATIVES,1	OtherE-mail,1	OtherInstitutions,1	OtherName,1	ParticipatedPrograms,1	PreliminaryQuestions,1	PreviousApply,1	PrimaryE-mail,1	ReadingAbility,1	RecommenderName,1	RECORDOFDEGREES,1	SchoolOrAffiliation,1	ScientificInterests,1	SemesterApplyingFor,1	SpeakingAbility,1	SubScore1th,1	SubScore2nd,1	SubScore3rd,1	TypeOfVisa,1	UndergrateInstitution,1	URLs,1	WritingAbility,1 ";
		String web_form="~1~	~zone~	~Zip-code or Post Code: *~	~Zipcode for Billing Statement~	~Zipcode~	~zipcheck~	~zip_C~	~Zip:~	~Zip Postal:~	~Zip Postal code: *~	~Zip Postal Code:~	~Zip Postal Code (required)~	~Zip Postal code~	~Zip code:~	~Zip Code :~	~Zip Code *~	~ZIP Code~	~Zip :~	~Zip *~	~Zip~	~Z~	~YTD total return:~	~Your Weight~	~Your Time Zone~	~Your State (US only)~	~Your Religion~	~Your race~	~Your Preference~	~Your personal ad's Text~	~Your Password:~	~Your Name:~	~Your Name~	~Your Interests~	~Your Height~	~Your Hair Color~	~Your Eye Color~	~Your E-mail Address:~	~Your email address:~	~Your Email~	~Your Details~	~Your Country~	~Your city town village:~	~Your City~	~Your billing information must exactly match the address where your credit card bill is sent. Billing First Name Billing Last Name Billing Country~	~Your billing information must exactly match the address where your credit card bill is sent. Billing First Name Billing Last Name~	~Your billing information must exactly match the address where your credit card bill is sent. Billing First Name~	~Your Age~	~Young child~	~You'll save 78% off the cover price! Plus get all the features of the Subscriber's Automatic Renewal Program as described below.~	~You are currently searching for:~	~You are a:~	~You are a~	~You are~	~Yield~	~YES, this is a~	~YES, register me for access to the Message Board~	~YES, I would like to receive up-to-the-minute order and shipping status information, plus super-deal, limited quantity close-out E-mail Specials. Your e-mail address is 100% safe and secure...we will never sell, trade or share it with anyone. *Please note* If you are already subscribed to the E-mail Specials, this will not change your subscription status.~	~Yes, I would like to receive the Lycos 50. This Pop Culture Barometer is an authoritative weekly list of the 50 most popular Internet search results, based on Lycos user searches.~	~Yes, I want to subscribe.~	~Yes No~	~Yes~	~Years:~	~Years with employer : Help~	~Years in previous residence :~	~Years in current residence: Help~	~Years Employed~	~Year of Birth:~	~Year~	~Yahoo! My Yahoo! Mail Make Yahoo! your home page~	~Yahoo Messenger:~	~y~	~x1~	~x~	~ws~	~Would you like to receive the WD-40 e-Tip of the Week via email?~	~Would you like to receive Fan Club announcements and the monthly WD-40 Fan Forum newsletter via email?~	~WorkPhone~	~Work Telephone:~	~Work Telephone Number~	~Work Phone: (xxx-xxx-xxxx)~	~Work Phone:~	~Work Phone (With Area Code):~	~Work Phone~	~Work Number:~	~Work Ext~	~WLS_ID~	~Witness Phone:~	~Witness Name:~	~Witness Address:~	~without the words~	~with the exact phrase~	~with at least one of the words~	~with all of the words~	~with a P E ratio from to~	~with a P E ratio from~	~with a market capitalization from $ to $~	~with a market capitalization from $~	~with a current trading volume between and~	~with a current trading volume between~	~Wireless Hardware Software Services~	~whowho~	~who_opened~	~who is from~	~who~	~White Caucasian~	~WhichComboPress~	~which~	~whereTo~	~Where would you like your trial subscription to be mailed:~	~Where do you buy most of your music?~	~Where do you buy digital music?~	~Where did you hear about us?~	~where~	~When To Call~	~When did you first become aware of the problem?:~	~Wheelchair accessible~	~What would you consider to be an amicable resolution to your complaint: [250 characters or less]~	~What type of relations you'd prefer~	~What response did you receive and why are you dissatisfied?~	~What other action have you taken regarding this complaint?~	~What kind of loan do you require?~	~What is your household's total annual income for the most recent calendar year? (Please combine income for all household members.)~	~What is the name of the school you last attended?~	~What do you think the council should do?~	~What best describes your work setting or occupation?~	~What are your hobbies and areas of interests? (check all that apply)~	~What are your favourite Hobbies and or Interests? You may select more than one answer by holding down the Ctrl key when you click the option.~	~Were you referred by anyone? If so, enter their account number here .~	~Welcome to SingleMe~	~Weight from~	~Website:~	~Website Search Enter Keyword s~	~WebLogicSession~	~WebCheckin~	~Web Site Enquiry~	~Warehouse Shipping~	~Want To Have Kids?~	~vwtotal~	~vwnitems~	~vw-form~	~vwentropy~	~vwcatalog~	~VTI-GROUP~	~Vogue may contact me via email regarding my subscription. Yes No Vogue may contact me via email regarding special Vogue promotions and events. Yes No Vogue may contact me via email regarding products or services from selected companies that Vogue thinks may be of interest to me.~	~Vogue may contact me via email regarding my subscription. Yes No Vogue may contact me via email regarding special Vogue promotions and events.~	~Vogue may contact me via email regarding my subscription.~	~visitcount~	~Visa~	~Vince Gill~	~viewMyMilesLoggedOut~	~View Hotels by City~	~Video Cable over IP~	~vid~	~Verify Password:~	~Verify Password~	~Verify Email:~	~Verify Email Address~	~Valid:~	~Utilities~	~useStartCal6~	~useStartCal5~	~useStartCal4~	~useStartCal3~	~useStartCal2~	~useStartCal1~	~useStartCal~	~usersession~	~UserSelectedSubSites~	~UserQuestion_required~	~usernamez~	~Username_required~	~Username: *~	~Username:~	~Username your password will be emailed to you~	~Username~	~User-ID (or Miles & More card number)~	~userExists~	~UserBanner1:txtErrorFlag~	~UserAnswer_required~	~User Name: (min. 4 characters)~	~User Name:~	~User Name~	~useEndCal~	~useCurrentEmail~	~Use this name instead of my first name:~	~Use Billing Address~	~URLQueryString~	~URL:~	~url~	~Upload Your Photo~	~Upload Photo:~	~Updated~	~update~	~Untitled~	~Unrestricted~	~united.com~	~u~	~Type of Trip:~	~Type of Stock~	~Type of Company~	~Type of account~	~Type and Category Rating: *~	~type~	~txtPrevStateRegion~	~txtPrevPostcode~	~txtPrevCityTown~	~txtPrevAddress2~	~txtPrevAddress1~	~TV~	~Turkish Airlines~	~Trisha Yearwood~	~tripLength~	~Tried to resolve your dispute:~	~TRAVEL SERVICES Flights Accommodation Transport Activities Insurance Student Travel Communications Travel Clothing & Gear Travel Services FAQ~	~Travel Deals Newsletter~	~Travel~	~Transportation~	~Trailing P E ratio less than or equal to:~	~Trading from $ to per share~	~Trading from $ to~	~Trading from $~	~trading~	~Tracy Lawrence~	~Town:~	~Town City~	~Town~	~Total price~	~Total Number of Rooms: *~	~Total number of persons~	~Total number of nights:~	~Tori Baxley~	~topnavsearch~	~token~	~toHtmlFields~	~tofes1~	~Toddler~	~Today's date~	~Toby Keith~	~To: City or Airport Code~	~To:~	~to Return Persons~	~To find cheap flights and airline tickets for holiday and business travel from the UK (inc. Heathrow airport) use the search form below. We now include Easyjet flights and RyanAir flights.~	~To City name or airport~	~To~	~tn_rooms~	~tn_results~	~Titles to display per page~	~Title:~	~Title~	~Timezone: *~	~Timezone:~	~Timezone~	~timetableForm~	~TimeOfDay~	~time1~	~time0~	~Time: second~	~Time: minute~	~Time: hour~	~Time: am pm~	~Time: :~	~Time:~	~Time Zone:~	~Time observed~	~time~	~tier~	~TID~	~Tickets:~	~Ticket Class~	~thisForm~	~this is the name you will use to access the system, between 6 and 16 characters, please use letters, numbers and underscores only. N.B. your username is case sensitive)~	~This information will be publicly viewable~	~This gives an indication of the field in which the company operates~	~This Fiscal Year Estimate~	~Then Sort By~	~theform~	~The Web MySpace~	~The Web~	~The value of a company's outstanding shares, as measured by shares times current price~	~The site is organised into several different zones . Select the one(s) you want to search from this list. (Ctrl-Click to select multiple categories)~	~The rate at which revenue grew from the previous 12 month period to the most recent one~	~The official site of the St. Louis Rams - Registration~	~The number of shares traded in a given number of days, divided by that number of days~	~The most recent quarter long-term debt divided by the most recent quarter common stock equity~	~The latest 12 months' earnings per share~	~the exact phrase~	~that are the market index chosen below by~	~that are currently their~	~that are currently~	~that are~	~Test & Measurement~	~Terri Clark~	~Tennis~	~template~	~Tell your friends about arabia.com Enter as many email addresses as you'd like, separating them using a comma (,). ( Optional )~	~Tell us about your complaint Please describe your problem with the bank in the space provided below. Limit your entry to 2,000 characters (approximately 30 lines). Remember not to include personal or confidential information as mentioned in our Privacy Notice at the top of this form.~	~Telephony~	~Telephone: *~	~Telephone:~	~Telephone Number~	~Telephone No.~	~Telephone :~	~telephone (optional):~	~Telephone (*)~	~Telephone~	~telefax (optional):~	~Teenager~	~Technology~	~tbQ2~	~tbQ1~	~task~	~Target~	~tafID~	~Systems Integration~	~System Engineer~	~system~	~SYLname~	~Swimming pool~	~suser~	~Survey Answer:~	~surname:~	~Surname~	~Supplier to Trade~	~Sugarland~	~Suffix~	~success~	~Suburb:~	~Subscripton form Send me the exclusive information tailored to association professionals. Please arrange a subscribtion to Association Manager magazine for: 6 issues @ ֳ‚ֲ�36.00 12 issues @ ֳ‚ֲ�67.00 18 issues @ ֳ‚ֲ�85.00~	~Subscribe!~	~Subscribe~	~submitted~	~Submit Information~	~Submit Form~	~Submit Complaint~	~Submit changes~	~Submit Application~	~Submit~	~Subject:~	~subject~	~Sub Category~	~sub~	~Street:~	~street, no.:~	~Street Name & Number:~	~Street and Apartment No.:~	~Street Address:~	~Street Address 2:~	~Street Address 1:~	~Street Address *~	~Street Address (1) :~	~Street Address~	~Street + number~	~Street & Number:~	~Street~	~Store_Code~	~StockCriteria~	~Stock sector:~	~Stock Price~	~Steve Azar~	~status~	~Stationer~	~StateValue~	~State:~	~State(USA):~	~State Zip~	~State Province: *~	~State Province:~	~State Province(Not USA):~	~State Province (for U.S. or Canadian addresses)~	~State Province~	~State Prov:~	~State of vehicle registration~	~State :~	~State *~	~State (US only)~	~State (if in USA):~	~State~	~startpage~	~start your search !~	~Start Search~	~Start in:~	~standardss~	~stage~	~Stafford (Student) Loan Application~	~st~	~SSN: (xxxxxxxxx)~	~SSN:~	~SSN~	~SSL~	~src_aid~	~src~	~sp-p~	~sportsbook~	~Sports Illustrated: Order Form~	~Sports & Outdoors~	~Sports~	~sp-f~	~Special partners offers~	~Spanish titles only~	~sp-a~	~SOURCEID~	~Source of Income: Help~	~Source of Income:~	~Source~	~Sort results by:~	~Sort By:~	~Sort by (for user defined view only)~	~Sort By~	~sort~	~Software Engineer~	~Software Development Tools~	~Software~	~Social Security Number~	~Social Insurance #~	~sms~	~smoking_pref~	~Smoking:~	~Smoking Preference:~	~Smoking Preference~	~Small Business~	~SkyMiles number and PIN~	~SKEY~	~siteSections~	~sitesearchboxform~	~SiteSearch~	~Sitename~	~SITEID~	~SITE SEARCH:~	~site~	~Single~	~Simulation Systems Integration Telephony Test & Measurement Transportation Utilities Other~	~Simulation~	~signupID~	~SignupForm~	~signup~	~Signature: This is a block of text that can be added to posts you make. There is a 255 character limit HTML is OFF BBCode is ON Smilies are ON~	~Signature: This is a block of text that can be added to posts you make. There is a 2000 character limit HTML is OFF BBCode is ON Smilies are ON~	~Signature: This is a block of text that can be added to posts you make. There is a 150 character limit HTML is OFF BBCode is ON Smilies are ON~	~Signature~	~Sign Up Now~	~Sign Up~	~Sign me up for the Rams Email Newsletter~	~Sign Me Up~	~SID~	~si~	~showTextBlock~	~showSummaryCheckBox~	~Show rates in:~	~Show newly arrived used books only~	~Show individual word scores~	~Show flights:~	~Shopping~	~Shop Name:~	~shipToDoneSuccessURL~	~shipToDoneErrorURL~	~shipToAddressName~	~Shipping Phone :~	~Shipping Method:~	~Shipping Address:~	~shipform~	~ship-address-from~	~Share Price:~	~Shania Twain~	~Sex:~	~Set title:~	~ses~	~Service~	~Senior Management (president, owner, CEO, chairman)~	~sender~	~Send me an e-mail copy of my receipt.~	~Send me a free merchandise catalog upon next mailing~	~Send~	~self_trav~	~selectList~	~select_dates~	~Select:~	~Select your Primary Occupational Classification *~	~Select Your Origin:~	~Select your Job Function *~	~Select Your Destination:~	~Select your Country~	~Select Type of? Residence:~	~Select Type of Loan:~	~Select Odds Preference~	~Select cabin class~	~Select an Industry~	~Select an Exchange~	~Select An Airport:~	~Select a start page~	~Seeking a:~	~seeking a~	~seek1~	~security_encode~	~Security Question:*~	~Section~	~Secret Question:~	~Secret answer:~	~secid~	~searchType~	~SearchTitleForm~	~searchSource~	~searchsection~	~searchMode~	~searchMethodHidden~	~SearchFormHeader~	~searchFormHandlerCaller~	~searchForm1~	~searchForm~	~SearchCatForm~	~SearchBoxID~	~search_start~	~SEARCH_REQ_REFERRAL_SOURCE_ID~	~SEARCH_REQ_REFERRAL_ID~	~SEARCH_BY~	~SEARCH:: in:~	~SEARCH::~	~Search:~	~Search within:~	~Search What?~	~Search Unrestricted Fares Only~	~Search Type:~	~Search Site:~	~Search Section:~	~Search Properties >>~	~Search only for pages written in:~	~Search one day before and after~	~Search Now~	~Search Non-Stop Flights Only~	~Search Illinois~	~Search Headline on:~	~Search For:~	~Search for Products~	~Search for exact title~	~Search for a Title~	~Search for a book~	~Search Flights~	~Search Date:~	~Search Byline on:~	~Search by:~	~Search by URL~	~Search by Schedule Price Passengers~	~Search by Category~	~Search by~	~Search [button]~	~Search !~	~Search  Site~	~Search~	~SDS.Item~	~scroll_y~	~ScreenerConfig~	~screener~	~Screen Name: This is the name that is seen by other members.~	~Screen~	~scratch~	~Scientist (R&D)~	~scheme~	~schedForm~	~SBCs~	~SaveReturnTime~	~SavePicUpTime~	~SaveHOUR1~	~Save login information in cookie file to bypass login page in the future.~	~Save~	~Salutation~	~Sales Revenue:~	~Sales Marketing Engineering Support Other~	~Sales Marketing~	~Sales Growth 1 Yr~	~Sales~	~SafeSearch~	~SAE_SEARCH~	~sae_qt1~	~Running Time~	~rte~	~rq~	~roundTrip~	~Round trip One way Multi city~	~Roomtype:~	~Rooms:~	~Rooms and Suites:~	~Room Type:~	~Room service~	~rID~	~rf~	~Reviews:~	~Revenue ($Mil)~	~Retype password:~	~returnYear~	~returnTime~	~ReturnRCompany~	~returnLocation~	~Returning~	~return_url~	~Return: second~	~Return: minute~	~Return: hour~	~Return: am pm~	~Return:~	~Return web pages updated in the~	~Return second~	~Return results where my terms occur~	~return results of the file format~	~return results from the site or domain~	~Return pages written in~	~Return on Sales~	~Return on equity (ROE) greater than or equal to:~	~Return on Equity~	~Return minute~	~Return Location~	~Return hour~	~Return Date: second~	~Return Date: minute~	~Return Date: hour~	~Return Date: am pm~	~Return Date:~	~Return date year~	~Return date month~	~Return date day~	~Return date~	~Return at:~	~Return am pm~	~Return : (dd mm yy)~	~Return (MM DD YYYY):~	~Return~	~ResultTemplate~	~Results per Page~	~ResultCount~	~Restaurant~	~resrvForm~	~Residential Address: (No PO Boxes)~	~resform~	~Reset Fields~	~Reset~	~RESERVE~	~RESELLERNAME~	~Research & Development~	~requirementsform~	~requiredVar[]~	~required~	~requestedTX~	~Requested User Name:~	~Requested Password:~	~request~	~Report Number:~	~Report Format:~	~Repeat Password:~	~Repeat Password~	~Repayment Period~	~RentalDetail~	~Rental Return Time second~	~Rental Return Time minute~	~Rental Return Time hour~	~Rental Return Time am pm~	~Rental Return Time~	~Rental Return Date (dd,mm,yr)~	~Rental Pick-Up Time second~	~Rental Pick-Up Time minute~	~Rental Pick-Up Time hour~	~Rental Pick-Up Time am pm~	~Rental Pick-Up Time~	~Rental Pick-Up Date (dd,mm,yr)~	~Rent or Mortgage Payment: Help~	~Reminder Phrase~	~Remember my Mileage Plus#~	~Remember me so I don't have to log in next time~	~Remember Me~	~Remarks~	~Religion:~	~relationshipStatus~	~Relationship pursuit~	~Registration Fee:~	~REGISTRATION~	~registerfrm~	~Registerform~	~register now!~	~REGISTER NOW~	~Register~	~RegionID~	~Region:~	~Region State~	~Region *~	~REGION~	~regFrm~	~regForm~	~regCombination~	~Reg.Account.NextFolder~	~Reg.Account.MainAction~	~refresh_captcha~	~refid~	~Referring Member:~	~referrer~	~referralID~	~refer_id~	~ref_temail~	~ref_sid~	~REF_PAGE~	~REF_ID~	~Re-enter your Password~	~Re-Enter Password:~	~Re-enter password~	~Re-enter mail~	~Re-enter Email:~	~redirect~	~Record_type~	~recipients~	~recipient~	~Receive News and Updates?~	~Receive free SMS alerts: (optional)~	~Reba McEntire~	~Reason for complaint:~	~Real Estate~	~Reading Program:~	~Reading level:~	~Reader age:~	~RBranchAddr~	~rates~	~r~	~quote~	~quicksearch_hl~	~quicksearch_fl~	~QuickFlight~	~Question:~	~QueryMode~	~QUERY_YEAR~	~Quantity~	~qualform~	~qt~	~qs~	~qp~	~qm~	~ql~	~qflight~	~qc~	~Q4~	~Q1~	~PwdPad~	~pw~	~Purchase time:~	~Purchase Price:~	~Purchase Order Number:~	~Publisher:~	~Publisher~	~Publication date:~	~PSTN Services~	~Province:~	~Province State:~	~Province~	~Provider or Publisher~	~protocol~	~Property:~	~Property Name: *~	~Promotion Code:~	~Promotion~	~PROMOCODE~	~Proj EPS Current Fiscal Yr~	~Profit Margin~	~productTypeCondition~	~productTypeCabin~	~productsearchForm~	~Production Mfng Engineer~	~Production~	~productID~	~Product number(s):~	~Product Name:~	~Product datas~	~PRODUCT~	~producing block trades of during~	~producing block trades of~	~PROCESSING_PARTNER_CODE~	~process_page2~	~Process Control~	~Prior Year's Sales~	~Prior Year's Net Income~	~Printer Selling Forms Direct~	~print_config~	~Primary Email~	~priceband~	~Price:~	~Price Sales Ratio:~	~Price Sales Ratio~	~Price Sales (TTM)~	~Price Range:~	~Price per item~	~Price Earnings Ratio:~	~Price Earnings per Share~	~Price Change Today~	~Price Cash Flow Ratio~	~Price Book Value per Share~	~Price Book Ratio:~	~Price Book Ratio~	~Price Book (MRQ)~	~Price~	~previousPageLink~	~PreviousInterestIDs~	~previous_action~	~Previous employment record~	~Previous Employer:~	~prev_vm~	~Pre-teen~	~Present Value:~	~Prepress~	~prePopulatedForm~	~Prepop~	~Prefix *~	~Preferred Ethnicity~	~Preferred Email Format~	~Preferred alliance~	~Preferred airline 3~	~Preferred airline 2~	~Preferred airline 1 Preferred airline 2 Preferred airline 3 OR Preferred alliance~	~Preferred airline 1~	~Preferred age range: *~	~pr~	~PPSX~	~ppm_signup1~	~PPFT~	~Postcode:~	~Postcode Zipcode :~	~Postcode Zip code~	~Postcode~	~postback~	~Postal Zip Code:~	~Postal Code:~	~Postal Code ZIP:~	~Postal Code~	~post code, town:~	~Post Code~	~Position or Title:~	~Position~	~Pop up window on new Private Message: Some templates may open a new window to inform you when new private messages arrive.~	~pokey~	~PLUS (Parent) Loan Application~	~Please write a brief description of the alleged violation, and provide any additional comments about the violation that you wish to submit:~	~Please specify your departure date:~	~Please specify your arrival date: Total number of nights:~	~Please send me news & updates about Lycos Network companies.~	~Please send me information about DMIA membership.~	~Please send me email updates about the proshop~	~Please send me a copy of the next Official Pittsburgh Steelers Merchandise Catalog~	~Please select one of the following:~	~Please retype your email address to confirm it:~	~Please notify me about new features, events and special offers about Excite services.~	~Please keep me informed of future offers and promotions from William Hill.~	~Please inform me about special offers from select Lycos clients.~	~Please indicate your preferences ֳ‚ֲ� if any - regarding work location, prospective companies to avoid etc.~	~Please Enter Your BetUS Account Password~	~Please Enter Your BetUS Account Number~	~Please enter the word you see from the picture below :~	~Please enter the text from the image above:~	~Please enter the answer to that question:~	~Please enter a hint question that we can ask if you forget your password:~	~Please enter 974445 into this field~	~Please contact me about marketing opportunities in OpenSystems Publications Please contact a marketing person at my company:~	~Please contact me about marketing opportunities in OpenSystems Publications~	~Please choose a password This must be at least eight (8) characters long, may contain numbers (0-9) and upper and lowercase letters (A-Z, a-z), but no spaces. Please make sure it's difficult for other people to guess.~	~plan_trip~	~PIN:~	~PIN Code~	~Pin (4 digit) Password (6-16 chars) (once) :~	~PicUpRCompany~	~Picture Upload:~	~Pick-Up Location~	~Pick up at:~	~Pick up : (dd mm yy)~	~pi_return_period2~	~pi_return_period1~	~pi_percent2~	~pi_percent1~	~pi_operator2~	~pi_operator1~	~pi_50day_avg~	~pi_200day_avg~	~phonesite~	~Phone: (*Required)~	~Phone:~	~Phone Number: (Digits only)~	~Phone Number:~	~Phone Number~	~Phone (if known)~	~Phone~	~pg~	~pff00000020000011~	~pff00000000010029~	~pff0000000001000e~	~Pets accepted~	~Personal Information~	~Personal Finance~	~performFlightSearch~	~Performance Table View~	~Perform Search~	~Perform Hotel Search~	~per_name~	~PEG Ratio:~	~pcode~	~PBranchAddr~	~paytype~	~Payment Method:~	~Payment details:~	~Pay Period~	~Paste your cv data here~	~Password2_required~	~Password_required~	~password_err~	~Password: This item is Confidential .~	~Password: *~	~Password: (min. 6 characters)~	~Password:~	~Password security question~	~Password security answer~	~Password confirmation:~	~Password (re-enter)~	~Password (or Miles & More PIN)~	~Password~	~passportID~	~Passengers Preferred cabin~	~part2of2~	~Parenting Children~	~ParamSet~	~parameters~	~pageNum~	~pageid~	~PageAction~	~PAGE~	~package_name~	~p1000000e~	~p_dref~	~P E-to-Earnings Growth Ratio (PEG) less than or equal to:~	~P E Ratio (TTM)~	~P E Ratio~	~P E Projected Nxt FY~	~p~	~ownerId~	~Owner: *~	~Other:~	~Other usefull information about hotel:~	~other title (optional):~	~Other State Province:~	~Other State (required if state is other)~	~Other Source:~	~Other Number:~	~Other members can see when I'm online? *~	~Other Email Address:~	~Other Business (required if business is other)~	~Other Business (required by US postal service)~	~Other (please specify)~	~Other~	~Organization:~	~Organization~	~Order #~	~Or, enter your destination City~	~or specify review code:~	~OR from to~	~OR from~	~OR~	~oq~	~Optional: Create a Care2 Connect profile~	~operators~	~Operating Systems~	~Opening: *~	~Opening:~	~OpenerParentRegionID~	~Only show refundable fares (Can be more expensive)~	~only search in this domain site:~	~online~	~OnePass Number:~	~One Way Round Trip~	~one or more of the following languages (select as many as you want).~	~on or after~	~Official site of the Pittsburgh Steelers - REGISTRATION .active {background-Color:#dedede;color:#5B5F62;border:1px ridge gold;} .inactive {}~	~OFFER/AIRLINE_TICKET/ITINERARY_TYPE_CODE~	~Offer Reference From Advert (If applicable)~	~oF~	~oe~	~Occupation:~	~Occupation :~	~Occupation~	~numRooms~	~numresults~	~Number:~	~Number of Years Company Has Been in Business:~	~Number of Stocks:~	~Number of Sales People at this Location:~	~Number of rooms:~	~Number of people:~	~Number of Passengers:~	~Number of adults: Children: Total number of persons:~	~Number of Adults:~	~Number of adults Children Total number of persons~	~Number of adults~	~Number and street:~	~Number and street~	~number~	~NumAdult~	~Nu~	~Ntk~	~nsfix~	~Notify on new Private Message:~	~Notify me of important news and offers from arabia.com and its partners.~	~Non-smoking rooms~	~none of these words~	~None of the above~	~no_no_thanks~	~No. of Results~	~No. of days:~	~No Children~	~nickName~	~nh~	~nextUrl~	~NextPage~	~Next page~	~next~	~newsletter~	~newclient~	~NewAccount~	~New User Registration~	~Networking H W & S W SBCs Software Software Development Tools Test & Measurement Other~	~Networking H W & S W~	~Net Income ($Mil)~	~nba~	~NAVIGATION/TEMP/FROMNEWSEARCH~	~Navigation/Required/Advanced_Search_Option~	~nav_form~	~Nationality: I.D Passport Number:~	~Nationality:~	~National Defense Magazine~	~Narrow the search to my preferred airlines~	~Narrator or Host~	~NameType~	~Name:~	~Name of the person you have contacted at this company:~	~Name of company you are lodging a complaint against:~	~Name of Company~	~Name of alleged violator Person or company (if known)~	~Name as on card~	~Name and Surname:~	~Name and ID #:~	~Name (*)~	~Name~	~NABLOCKURL~	~My username:~	~My Smoking Habits:~	~My sexual orientation:~	~My Relationship Status:~	~My Job Function is: Senior Management (president, owner, CEO, chairman) Management Sales Marketing Customer Service Production Warehouse Shipping Prepress IT Accounting Administrative Other:~	~My Height:~	~My Favorite Rams Player is~	~My email address:~	~My Body Type:~	~My birthday:~	~mv_successpage~	~mv_shipmode~	~mv_session_id~	~mv_order_profile~	~mv_nextpage~	~mv_failpage~	~mv_doit~	~mv_click_Previous~	~mv_click_Next~	~mv_click_map~	~Music~	~Multimedia~	~MSN Messenger:~	~ms~	~Mr.~	~MPIdentifier~	~Movies,Theater & TV~	~Mother's Maiden Name :~	~Mother's maiden name~	~Morningstar stock type:~	~Morningstar equity style box:~	~More than 5,000 1000 to 4,999 500 to 999 100 to 499 50-99 Less than 50~	~Months Employed~	~Monthly Income~	~Monthly frequency usage:~	~MONTH and DAY you were born:~	~module~	~Model~	~mode~	~MobileNumber~	~Mobile:~	~Mobile Telephone Number~	~Mobile Phone:~	~Mobile Phone Number (no dashes or spaces)~	~Mobile Phone Number~	~Mobile Phone :~	~Mobile Number~	~Mobile~	~Mkt_Id~	~missing_fields_redirect~	~Minimum market capitalization:~	~MinAge~	~Min: Max:~	~min:~	~Min~	~Military Affiliation~	~Militarized Ruggedized~	~miles_miles~	~MileagePlus~	~Mileage Plus#~	~Middle Initials:~	~Middle Initial:~	~Middle Initial~	~MI~	~Mezzanine Boards~	~Menu1:txtLastMenu~	~Membership Name:~	~Member Name:~	~Member Name~	~Member Login Username: Password:~	~Member Login Username:~	~Member ID~	~Meeting rooms~	~Measures the price performance of a stock in comparison to all other stocks~	~MCA - Mercury Dreamworks~	~maxResultsOnPage~	~Maximum rate per room night:~	~MaxFlights~	~MAX_FILE_SIZE~	~max_connections~	~max: %~	~max:~	~Mature Content Filter~	~matchMainForm~	~Match first part of words~	~Mass Storage~	~Marketing~	~Market Research~	~Market Capitalization ($Mil)~	~Market Capitalization~	~Market Cap:~	~Marital Status: Help~	~Marital Status: * Help~	~Marital Status:~	~Marital status~	~Manufacturers:~	~Manufacturer~	~Management~	~Male Female~	~Male~	~Make~	~MAINFORM~	~mailsitename~	~Mailing Preferences~	~Mailing Address: (If different)~	~Mailing Address:~	~Mailing Address 2:~	~Mailing Address 1:~	~Mailing address~	~Mailbox:~	~mail_language~	~Mail Stop (if applicable):~	~Mail Stop~	~magazine_abv~	~Magazine & Books~	~m_RI~	~m_RC~	~m_PR~	~m_LRC~	~m_LANG~	~m_ES_45~	~m_ES_2~	~m_DL_FREE_Mail~	~m_CBURL~	~m_CBERRURL~	~m_AL~	~lsid~	~Lot Number (LOT):~	~Looking for: (choose all that apply)~	~looking for a~	~Look for a:~	~LOOK~	~long-path~	~logintype~	~logininform~	~loginform~	~loginboxform~	~Login Now~	~Login :~	~Login~	~LogID~	~Location:~	~Location~	~Locale~	~Local Timezone:~	~Local Number~	~LoadQuestion~	~lk~	~Living status~	~listenerId~	~List the causes most important to you:~	~LICENSEEID~	~Licensee~	~License plate number~	~lg~	~Lexile:~	~level~	~Lender Preference~	~Leisure activities and interests~	~Lee Ann Womack~	~Leaving from Departure date Time second~	~Leaving from Departure date Time minute~	~Leaving from Departure date Time hour~	~Leaving from Departure date Time am pm~	~Leaving from Departure date Time~	~Leaving From (city or airport)~	~Leaving from~	~Leaving~	~Leave second~	~Leave minute~	~Leave hour~	~Leave am pm~	~Leave~	~ldo~	~lc~	~Latest Closing Price~	~LastCountryRegionID~	~Last_name_C~	~Last:~	~Last Quarter's Earnings~	~Last name: *~	~Last name: (*Required)~	~Last Name:~	~Last Name :~	~Last name *~	~Last name~	~Last High School Attended (required by US postal service)~	~Last Fiscal Year Earnings~	~Last Dividend~	~Language:~	~language~	~langswitch~	~lang~	~Landing time:~	~la~	~l_vid~	~l_contentid~	~l_cid~	~l_bid~	~Keywords~	~KeyByVON~	~k~	~JunkMail~	~jsp_name~	~jsok~	~js~	~Jr. Sr.:~	~Journey Type~	~Josh Turner~	~JoinForm~	~Join Our Hot Deals Newsletter~	~Join now!~	~Join freeֳ‚ֲ�>>~	~Join free online community SearchYourLove~	~Job Title:~	~Job Title~	~Jimmy Wayne~	~Jessica Andrews~	~Jedd Hughes~	~itemID~	~Item Type:~	~IT~	~isSearch~	~ISBN:~	~ISBN~	~Is the product available for evaluation?~	~iPromotion~	~IP Telephony~	~Invoice# or RMA#:~	~inviterID~	~invite~	~Introduce Yourself: What is the first thing you want people to know about you? Need help?~	~Intraday Volume~	~Into this box:~	~Internet H W & S W~	~internet access. If yes, enter your user logon name~	~Interests:~	~Interest level:~	~Interest~	~Interactive TV Email :~	~Insurance >>~	~inputForm~	~Initials~	~Initial:~	~Information about location:~	~Inform me of special offers from the Rams and partners~	~Infant:~	~Infant(s)~	~Infant (7 Days-2 yrs )~	~Infant~	~Industry:~	~Industry~	~Industrial Control~	~Indicated Dividend Rate~	~index1~	~Index Membership~	~index~	~Inclusion in one of the Standard & Poor's Indices~	~Inclusion in one of the Dow Jones Indices~	~Include Your Photo: Profiles with photos get 8 times more results!~	~Include this message with my gift:~	~Include airports within 80 miles~	~incl_subsites~	~Incident Details:~	~Incident Date:~	~In what format would you like to receive emails?~	~in price by % or more during~	~in price by~	~img_name~	~I'm applying for a job as...~	~iID~	~If you would like to receive information from our carefully selected partners, please tick this box.~	~If you have an ONLINE Gift Certificate, please enter it here:~	~If you have a promotional code or coupon, please enter it here:~	~If you have a K-Swiss gift certificate code, please enter it here:~	~If you have a coupon code for a special offer, please enter it here:~	~If 'Yes', would you please tell us who dealt with you and when?~	~If the box below is empty it means Money Mart does not have a store in your city. Please choose the city nearest to you.~	~ie~	~I'd rather not say~	~I'd Prefer To Meet:~	~ID Number:~	~Id~	~ICQ Number:~	~I.D Passport Number: Nationality:~	~I.D Passport Number:~	~I would like to see future articles about:~	~I want to recieve Nightclub & Bar Magazine~	~I want to recieve Nightclub & Bar~	~I prefer to view my account in:~	~I prefer to receive advertising email that pays me.~	~I prefer non-stop flights~	~I own or work with a small business~	~I O Boards~	~I keep kosher:~	~I hereby give permission to verify the above information for a credit decision based on the verified information. In accordance with the, Electronic Signatures in Global and National Commerce Act, by typing my name in the signature box below I understand it is as legally valid and binding as a written signature. I also certify that the above information is true to the best of my knowledge.~	~I hereby confirm that I have read, understood and agree to be bound by all the~	~I have read and agree to the terms and conditions~	~I have read and agree to the Terms & Conditions of the service~	~I have read and agree to the~	~I have read and agree to all the notices and disclosures above.~	~I have read and accept your~	~I have read and accept the Data Protection Notice above~	~I have read and accept the~	~I have attended a Steelers game at Pittsburgh~	~I have attended a Rams Game in St.Louis~	~I Go To Synagogue:~	~I don't mind if you contact me Please don't contact me~	~I confirm that I have read and agreed to the~	~I am looking for:~	~I am a: *~	~I am a:~	~I am a Steelers Season Ticket Holder~	~I am a Rams Season Ticket Holder~	~I am a~	~I am 18 years of age or over~	~I AGREE. START MY FREE TRIAL~	~I agree to the~	~I agree to be contacted from time to time regarding customer~	~I agree that I am at least 18 years old and have read and agree to the~	~I Agree~	~I accept William Hill's~	~https://www.youbet.com/account/signupForm.asp?~	~https://www.willhill.com/iibs/EN/updatedetails.asp?type=join~	~https://www.printsolutionsmag.com/subscribe_form.html~	~https://www.printsolutionsmag.com/form_actionSubscribe.asp~	~https://www.moneymart.ca/paydayloans/applynow.asp~	~https://www.mgear.com/Pages/Account/AccCheckOutNew.asp?mode=checkout&store=MG&CartId=534721~	~https://www.kswiss.com/cgi-bin/kswiss/store/ord/cart_cust_info.html~	~https://www.hilohattie.com/acb/basket/update.cfm?&User_ID=10647832&St=2615&St2=-62053359&St3=-92642778&DS_ID=2&DID=9~	~https://www.fusemail.com/order/order.html~	~https://www.firstmeritib.com/QuickLoanApplication.aspx~	~https://www.finishline.com/store/checkout/shipping.jsp;jsessionid=AG2UFZAOKRFGFLAQAJXCFF3MCABG4IV0?_requestid=315249~	~https://www.financial-securesite.com/AmericanUnsecured/LoanApp1.aspx?LT=P&AID=null~	~https://www.filastore.com/catalog/shippingAddress.cfm?TID=8687-35190510403235100449380-0&module=cart&action=newUnregisteredcheckout~	~https://www.betdirect.net/betdirect?action=go_register~	~https://wp.eurobet.com/oaecu2.go?&lang=20&st=1&channel=sb&p_ref=&stage=100~	~https://w1.buysub.com/servlet/OrdersGateway?cds_mag_code=VOG&cds_page_id=1242&cds_response_key=I3ANT5AA~	~https://subs.timeinc.net/SI/si_tswtho0206.jhtml?experience_id=127419&source_id=7~	~https://subs.timeinc.net/PE/pe_0504.jhtml?experience_id=72304&source_id=7~	~https://ssl.northernweb.net/northlandmarine/merchant/merchant.mv?Session_ID=426CEA5D000A37C400007A6E00000000&Screen=OINF&Store_Code=NM~	~https://shop.sae.org/calendar/topreg.shtml~	~https://secure2.steelers.com/reg?securejump=1~	~https://secure.sportsinteraction.com/sportsbook/body_accountform.cfm~	~https://order.store.yahoo.com/cgi-bin/shipping-form?unique=d25fe&catalog=flipflop&et=426d2d06&basket=b%3D5C518088d8001e49426d25fac8156bea9a3421d0144849ce188a984368822122d%26l%3D%26s%3DOb1rev_nTcVtFlm1gHPIPeFHj_c-~	~https://op.oxpub.com/scripts/subscribe_1.php~	~https://magshop.co.nz/RegisterP.asp~	~https://magshop.co.nz/Register.asp?Error=Last+Name+is+a+required+field+and+has+not+been+filled+in+properly%2E&NextPage=%2FDelivery%2Easp~	~https://accountservices.passport.net/reg.srf?id=2&sl=1&vv=400&lc=1033~	~https://accountservices.passport.net/reg.srf?id=2&sl=1&lc=1033~	~http://www2.inmail24.com/Regist1.htm~	~http://www2.cityofseattle.net/util/forms/surfacewater/swq_form.htm~	~http://www2.cityofseattle.net/util/forms/surfacewater/SendSwqForm.asp~	~http://www.webdate.com/register.php~	~http://www.washingtonpost.com/wp-adv/archives/advanced.htm~	~http://www.venere.com/cgi/ihr/vcom/padd.php?lg=en~	~http://www.vegas-sportsbetting.com/signup.html#signup~	~http://www.ual.com/~	~http://www.tio.com.au/complaint_form.htm~	~http://www.thy.com/en/index.php~	~http://www.syl.com/join/~	~http://www.stlouisrams.com/Reg/~	~http://www.state.il.us/dfi/ccd/ccd_complaint.htm~	~http://www.sportsmansguide.com/checkout/checkout_address.asp~	~http://www.singleme.com/ppmdating.cfm~	~http://www.singleme.com/ppm_signup2.cfm?CFID=153993029&CFTOKEN=458866f-c8d11f58-486b-4111-8601-06594f88c846~	~http://www.secure-reservations.net/hotels/hotels.mvc?agent_id=1076&state=AZ&country=US~	~http://www.royal-plaza.co.il/ContactEng.html~	~http://www.rentdirect.co.il/reservationsENG/RentalDetail.asp~	~http://www.priceline.com/flights/default.asp?irefid=HPAIRQT&irefclickid=ADSEARCHLINK&AirASO=Y~	~http://www.powells.com/search.html~	~http://www.postmaster.co.uk/cgi-bin/nav/register_form.pl?productcode=PML001~	~http://www.orbitz.com/App/ViewRoundTripSearch?&retrieveParams=true&z=35ff&r=17~	~http://www.opensystems-publishing.com/subscriptions/new/digital_download/~	~http://www.omisan.com/omisanonline/form/complaint_form.htm~	~http://www.omisan.com/cgi-bin/FormMail.pl~	~http://www.omeda.com/cgi-win/von.cgi?add~	~http://www.omeda.com/cgi-win/von.cgi~	~http://www.no-fax-loan.com/?source=dbdirect&ovmkt=D90HV4IAQRS0K6QP3HT9DITP04&ppcseid=2281&ppcsekeyword=payday+loan+application~	~http://www.nightclub.com/subscribe/~	~http://www.netcheck.com/complaint.htm~	~http://www.netcheck.com/cgi-bin/complaintform.cgi~	~http://www.nationaldefensemagazine.org/subscribe/trial.cfm~	~http://www.myreg.net/checkform?force_form=1&form_id=6165~	~http://www.motels.com/~	~http://www.miss-janet.com/index.phtml~	~http://www.marketwatch.com/tools/stockresearch/screener/default.asp?state=2&siteid=mktw~	~http://www.maineadvantage.com/forms/request.htm~	~http://www.lonelyplanet.com/travel_services/~	~http://www.knag.nl/formulieren/shop_form.html~	~http://www.klm.com/travel/il_en/index_default.html~	~http://www.italyhotelink.com/add.htm~	~http://www.hotellocators.com/~	~http://www.gravesham.gov.uk/index.cfm?articleid=389~	~http://www.gonegambling.com/membersonly/membersregister.html~	~http://www.globeinvestor.com/v5/content/filters~	~http://www.glendaleaz.com/Police/loader.cfm~	~http://www.ghchealth.com/forum/profile.php?mode=register&agreed=true~	~http://www.frbatlanta.org/consumer/complaint.cfm~	~http://www.financialaid.com/halo/app_qualification.cfm?id=gosc_Financial_Aid_co&ct=0,0&P=0~	~http://www.experienced-people.co.uk/jobseekers.htm~	~http://www.eldan.co.il/CalcTotal.asp~	~http://www.eldan.co.il~	~http://www.elal.co.il/default.asp?V_DOC_ID=700&V_LANG_ID=0&initiateProfile=true~	~http://www.eatpoo.com/phpBB2/profile.php?mode=register&agreed=true~	~http://www.eatpoo.com/phpBB2/profile.php~	~http://www.dream-dating.org/signup.php~	~http://www.doubleclickloans.co.uk/php-box/process_first_form.php~	~http://www.digitallook.com/cgi-bin/digital/stock_screener.cgi~	~http://www.debtbusterloans.com/loanapplication1.aspx~	~http://www.dating.com/home.asp?sec=content&sub=3~	~http://www.datemeister.com/page/en/signup/start?dmsess=b403853f7e1de533786430130c552830~	~http://www.date.com/~	~http://www.danielhotel.com/reservations.asp~	~http://www.cybersuitors.com/ServerSide/Cybersuitors/Membership/ConfidentialDetails.asp?Join=yes~	~http://www.cybersportsbook.com/join.asp~	~http://www.cupidusa.com/servlet/RegistrationServlet~	~http://www.cupidusa.com/jsp/join.jsp~	~http://www.couponforum.com/profile.php?mode=register&confr=true~	~http://www.ci.glendale.az.us/Police/On_LineComplaintForm.cfm~	~http://www.cashette.com/myCashette/createNewUser.jsp?~	~http://www.budget.co.il/rates.asp~	~http://www.bettingexpress.com/sportsbook/join.html~	~http://www.bet-at-home.com/en/register.asp~	~http://www.besthotel.com/?src_aid=161203~	~http://www.bbltamex.com/Aboutus/pages/careers/sendcv.asp~	~http://www.autoeurope.co.il/reservations/INDEX.HTM~	~http://www.audible.com/adbl/site/advancedSearch/advancedSearch.jsp?BV_SessionID=@@@@1758199841.1060006119@@@@&BV_EngineID=ccciadcimmkkhgmcefecegedfhfdfom.0&uniqueKey=1060006121723~	~http://www.associationmanager.co.uk/utility/subscribe/index.asp~	~http://www.associationmanager.co.uk/utility/subscribe/~	~http://www.americanairlines.com/~	~http://www.amazon.com/exec/obidos/ats-query-page/ref=b_bh_1_a_2~	~http://www.am630.net/skin/lclub/register/reg_user_info.php~	~http://wbln0018.worldbank.org/acfiu/acfiuweb.nsf/externalsubmission?OpenForm~	~http://travel.kelkoo.co.uk/b/a/c_172201_flights.html~	~http://subscribe.americancityandcounty.com/dnnThankYou.asp~	~http://subscribe.americancityandcounty.com/dnnsubform.asp~	~http://shopping.spa.net/Stores/TTBabyShoes/Register.asp?HSD=1&ExpressCheckout=True~	~http://search.sky.com/search/skynews/results/1,,,00.html?QUERY=&CID=30000&x=13&y=7~	~http://search.scotsman.com/scripts/rwisapi.dll/@scotsman.env?CQ_QUERY_STRING=&CQ_USER_NAME=guest&CQ_PASSWORD=guest&CQ_PROCESS_LOGIN=YES&CQ_LOGIN=YES&CQ_SAVE[Refine_the_query]=FALSE&CQ_DTF_SEARCHFORM=YES&CQ_CUR_LIBRARY=Arts_Lib%20Careers_Lib%20Heritage_Lib%20Leisure_Lib%20Motors_Lib%20Property_Lib%20Travel_Lib%20ScottishLocalities_Lib%20Festival_Lib%20Network_lib~	~http://search.atomz.com/search/~	~http://search.abcnews.go.com/query.html?col=&ht=0&qp=&qs=&qc=&pw=100%25&ws=0&la=&si=1&fs=&qt=&ex=&rq=1&oq=&qm=0&ql=a&st=1&nh=10&lk=1&rf=1~	~http://screen.morningstar.com/StockSelector.html?wmcsection=toolsssel~	~http://screen.finance.yahoo.com/stocks.html~	~http://res.findlocalhotels.com/nexres/search/power_search.cgi?header=search&src=10009560~	~http://registration.excite.com/excitereg/register.jsp?return_url=http%3A%2F%2Fe21.email.excite.com~	~http://registration.excite.com/excitereg/register.jsp~	~http://rapids.canoe.ca/cgi-bin/reg/NR-register.pl?MODE=CANOEMAIL_INFO&LOOK=CANOEMAIL&PRODUCT=221&ACTION=PROCEED&REF_PAGE=%20&FORM_ID=CANOEMAIL~	~http://prosearch.businessweek.com/businessweek/general_free_search.html~	~http://passport.care2.net/signup.html?_promoID=1&pg~	~http://passport.care2.net/signup.html~	~http://moneycentral.msn.com/investor/finder/customstocksdl.asp~	~http://login.myspace.com/index.cfm?fuseaction=join.step1mod&nextPage=fuseaction%3Dmail.inbox%26MyToken%3Deb5283ea-7bb7-46ce-aaf4-851837ad2c8d~	~http://linesmaker.com/join.php~	~http://ldbreg.lycos.com/cgi-bin/mayaRegister?m_RC=32&m_NP=FREE_Mail&m_PR=27&IC=1~	~http://hotel.de/Search.aspx?lng=EN~	~http://forums.sijun.com/profile.php?mode=register&agreed=true~	~http://finance.aol.com/usw/quotes/stockscreener~	~http://fanclub.wd40.com/register2.cfm~	~http://edit.travel.yahoo.com/flights.html?source=YG~	~http://dbase2.opensystems-publishing.com/fmi/xsl/subs_america/browserecord.xsl~	~http://booking.elal.co.il/servlet/FlowcontrolServlet~	~http://betus.com/english/join.asp~	~ht~	~hs_validate~	~hs_searchmode~	~HP_QUICKSEARCH_USED~	~How much would you like to borrow?~	~How have you known about us?~	~How did you learn about the Maine Advantage Education Loan?~	~How did you hear about us?:~	~How did you hear about us?~	~How did you hear about us:~	~How did you hear about Sports Interaction~	~How did you hear about Postmaster?~	~How did you hear about Littlewoods Bet Direct? TV Football Pools Coupon Newspaper, Search Engine Bet 247 Website Radio Lotteries Other, Please specify: Newspaper (please specify which) :~	~How did you hear about Littlewoods Bet Direct? TV Football Pools Coupon Newspaper, Search Engine Bet 247 Website Radio Lotteries Other, Please specify:~	~How did you find us?~	~How did you find our website?~	~Household size:~	~Household Income:~	~Household income~	~House Number Name~	~House Name Number~	~HotelSearch~	~hotelpage~	~hotel_data~	~Hotel, Condo & Bed & Breakfast Reservations~	~Hotel Type:~	~Hotel Reservations~	~Hotel Quick Search:~	~Hotel only~	~Hotel Name:~	~Hotel Group:~	~Hotel Chain:~	~Hotel Brand:~	~Hot Apple Pie~	~hostname~	~HomePhone~	~Homepagesearch~	~Home Telephone:~	~Home Telephone Number~	~Home Phone: (xxx-xxx-xxxx)~	~Home Phone:~	~Home Phone *~	~Home Phone (With Area Code):~	~Home Phone~	~Home or Small Office (SOHO) Hardware Software Services~	~Home & Gardening~	~Home & Family~	~hl~	~Hint Question:~	~Hilo Hattie~	~Highest level of education~	~High School Last Attended (required by US postal service)~	~Hide your online status:~	~hFNum~	~help ). appearing and or not appearing and or not appearing~	~help ). appearing and or not appearing and or not~	~help ). appearing and or not appearing~	~help ). appearing and or not~	~help ). appearing~	~help ).~	~Height:~	~Height from~	~Health & Wellness~	~Health & Pharmaceuticals~	~Health~	~Headline news from Sky News - Witness the event Search Sky~	~header~	~hdnPage~	~hdnNav~	~hdnItinKey~	~hdnItinDetailKey~	~hdnDNav~	~hdnAddressID~	~hdnAction~	~Have you raised this problem with the council before?~	~Have you contacted the TIO about this problem previously? If so, who did you speak to at the TIO and what is your TIO reference number?:~	~Hardware Engineer~	~Hanna-McEuen~	~Hank Williams Sr.~	~handler~	~Hair Color:~	~h~	~gts~	~Gross Profit Margin~	~Gross Monthly Salary*~	~Gross Monthly Income:~	~GOVERNMENT:~	~Government Electronics~	~GOTO~	~Google Search~	~Golf~	~Going to Return date Time second~	~Going to Return date Time minute~	~Going to Return date Time hour~	~Going to Return date Time am pm~	~Going to Return date Time~	~Going To (city or airport)~	~Going to~	~Go!~	~Go To Step 3 >>~	~Go To Step 2 :ֳ‚ֲ�ֳ‚ֲ�Payment Information~	~go search site~	~GO~	~globalSearchForm~	~gift-wrap~	~gID~	~get rate~	~George Strait~	~geo_id~	~General Purpose Computers~	~Gender:~	~Gender~	~Gary Allan~	~Game room~	~Gambling & Lottery~	~Gambling~	~g~	~function clearKeywords(elt) { if (elt.value.indexOf(eg:) == 0) { elt.value = ; } } function restoreInstr(elt) { if (elt.value == ) { elt.value = eg: blue, classic; } }~	~Full name:~	~Full name~	~FrontPage_Form1~	~fromform~	~from_address~	~From: City or Airport Code~	~From: (month day year) To: (month day year)~	~From:~	~From To~	~from Outbound Airlines~	~From City name or airport~	~From % To~	~From $ To $~	~From $~	~From~	~frmSwqComplaint~	~frmStep1~	~frmSignup~	~frmSearch~	~frmQuickSearch~	~frmOtherKLMsites~	~frmMain~	~frmLs~	~frmLogin~	~frmJoin~	~frmForm1~	~frmDestinations~	~frm~	~Friend's Username~	~FREE_SUB~	~Free Samples, Discounts & Coupons~	~Free Parking~	~Fractional Decimal~	~fr~	~FPGAs~	~FormName~	~formID~	~formEvent~	~formClass~	~Format:~	~formActionType~	~formAction~	~form1~	~FORM_submitType~	~FORM_ID~	~Form~	~forid~	~for documents that~	~for~	~Footer1:txtServerErrorFlag~	~Football Pools Coupon~	~Food & Beverage~	~FocusField~	~fmRegister~	~fmBookingRadio~	~fmBooking~	~FlightStatus~	~FlightSearch~	~Flights, Cheap Flights, Airline Tickets, International Flights, Last Minute Flights~	~Flights~	~flight_number~	~flight_date~	~Flight Number: Departure Date:~	~Flight Number:~	~Flight number Leaving from Going to~	~Flight number~	~Flight No.:~	~Flight class~	~flifoForm~	~Five Things I Can't Live Without:~	~Fitness center~	~FirstTime~	~First_name_C~	~first_capture_id~	~First:~	~First name: *~	~First name: (*Required)~	~First Name:~	~First Name(s)~	~First Name :~	~First name *~	~First name (*)~	~First Name~	~First Mortgage Balance:~	~first~	~findAddr~	~Find:~	~find your car~	~Find web pages that link to~	~Find web pages similar to~	~Find titles added to the site between: and~	~Find titles added to the site between:~	~Find Stocks~	~Find products on the web~	~Find pages that link to the page~	~Find pages similar to the page~	~Find Hotels~	~Find Address~	~Find a Broker~	~Financing & Investing~	~Financial Services~	~fileKey~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/Yahoo! Advanced Web Search.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/www.linuxmail.com.html~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/www.dbzmail.com.html~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/www.boardermail.com.html~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/The Orchid Thai - Style Resort Hotel in Eilat, Israel - Reservations.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/Neptune Eilat Hotel  Secure Reservation.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/myfunnymail_com - mail.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/Mevo Jerusalem Tower - Residential Hotel - Secure Reservation.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/Match_com Millions of possibilities to meet your match.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/Lufthansa Deutschland.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/jdate_com - the world's largest Jewish singles community.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/Google Advanced Search.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/Galei Eilat Hotel  Secure Reservation.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/Delta Air Lines - Travel, Airfare and Airline Tickets on delta_com.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/Continental Airlines - Airline Tickets, Vacations Packages, Travel Deals, and Company Information on continental_com.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/British Airways - home page.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/Book search - Sagebrush Corporation.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/arabia_com - Registration.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/americansingles_com - Free personals, online dating, chat, millions of members.htm~	~file:/C:/Documents and Settings/Eyal_ami/Desktop/A b s o l u t e A g e n c y.htm~	~File Format~	~Fila Store - The Official Store of Fila~	~field-ignore-date~	~field-dateyear~	~field-datemod~	~Field:~	~Field Buses~	~Fiction:~	~fic_53681_53720_fieldName~	~fic_53681_53718_fieldName~	~fic_53681_53717_fieldName~	~fic_53681_53716_fieldName~	~fic_53681_53715_fieldName~	~fic_53681_53714_fieldName~	~fic_53681_53712_fieldName~	~fic_53681_53710_fieldName~	~fic_53681_53709_fieldName~	~fic_53681_53707_fieldName~	~fic_53681_53706_fieldName~	~fic_53681_53705_fieldName~	~fic_53681_53704_fieldName~	~fic_53681_53703_fieldName~	~fic_53681_53701_fieldName~	~fic_53681_53700_fieldName~	~fic_53681_53699_fieldName~	~fic_53681_53698_fieldName~	~fic_53681_53697_fieldName~	~fic_53681_53696_fieldName~	~fic_53681_53695_fieldName~	~fic_53681_53694_fieldName~	~fic_53681_53693_fieldName~	~fic_53681_53691_fieldName~	~fic_53681_53690_fieldName~	~fic_53681_53689_fieldName~	~fic_53681_53688_fieldName~	~fic_53681_53687_fieldName~	~fic_53681_53686_fieldName~	~ff_ct~	~Females~	~feet~	~Federal Reserve Bank of Atlanta~	~fb_tariftype~	~fb_directonly~	~fb_class~	~Fax:~	~Fax over IP~	~Fax Number: (Digits only)~	~Fax Number:~	~Fax No.~	~Fax~	~Fashion & Lifestyles~	~Fashion & Beauty~	~Fare Type:~	~Fabrics~	~f2~	~f1~	~f0~	~f_OnLineComplaintForm_53663_53679~	~f[c2csections][]~	~f~	~Eye Color:~	~Extra driver~	~Ext.~	~ExpressCheckout~	~Expiry date~	~Expires (mm yy):~	~Expiration Year~	~Expiration Month~	~Expiration Date:~	~Expiration date~	~Expiration :~	~Expedia.com~	~expandTravelers~	~Existing Email Address:~	~Executive (Owner Pres.)~	~Excite may make the information that I supplied available to selected companies so that they may contact me regarding services that may be of interest to me.~	~Evening Phone:~	~Ethnicity:~	~Ethnicity Background:~	~Ethnic Origin:~	~et~	~Estimated Monthly MC VISA Volume~	~Est. 5 Yr EPS Growth:~	~Est. 1 Yr EPS Growth:~	~Essay: About Meֳ‚ֲ�(min 100 characters)~	~EPS Growth 1 Yr~	~EPS (TTM)~	~entryurl~	~Entertainment Celebrities~	~Entertainment & Games~	~Enter your Email:~	~Enter the letters as they appear in the box above:~	~Enter the date you're next paid (mm dd yyyy) :~	~Enter the code from the image on the right into the box below:~	~Enter the code below:~	~Enter promotion code:~	~Enter Pin Password again :~	~Enter author, title or keyword~	~Enter a short motivation letter here~	~Engineering Support~	~Engineering Manager~	~Engineering Consultant~	~engine~	~EngHeb2~	~EngHeb1~	~Employment:~	~Employment Type:~	~Employment Status~	~Employment Sector:~	~Employer Name~	~EmbPromo~	~EmailLite~	~emailFormatType~	~Email_C~	~Email: *~	~E-mail:~	~Email:~	~E-mail*~	~Email Addressֳ‚ֲ�~	~Email Address: This item is Confidential . You'll need it to login.~	~E-mail address: *~	~Email address: *~	~Email address: (*Required)~	~E-mail address:~	~Email address:~	~E-mail Address :~	~E-mail address (example: janedoe@yahoo.com)~	~E-mail Address~	~Email Address~	~E-mail *~	~Email *~	~Email (required)~	~E-Mail (*)~	~E-mail~	~Email~	~Electronic Components~	~eId~	~ei~	~Educator Student Librarian~	~Educational~	~Education:~	~Education level:~	~Education and qualifications~	~Education~	~EditFlag~	~EBT_FORM_ID~	~EAPID~	~dummy~	~dumi~	~DSP Resource Boards~	~DSP Chips Cores~	~DSP Applications~	~Dry cleaning laundry~	~dropmsgform~	~Driver's age~	~Drinking:~	~DptText~	~dpHidden~	~DONEURL~	~done~	~Domicile Route~	~domain~	~dod_yy~	~doa_yy~	~do_not_check~	~Do you want us to send you tips, reminders, information about scholarships and other ways to help pay for college?~	~Do you want to receive new profiles?~	~Do you smoke?~	~Do you influence the purchase of Voice Fax Email Video over IP and or telecommunications and or computer products or services for your organization, clients or customers?~	~Do you drink?~	~Do you currently pay a mortgage to a bank or building society?~	~Do not save username~	~dl~	~DJIA Nasdaq Composite Russell 2000 DJ Internet Index~	~Dividend Yield:~	~Dividend yield greater than or equal to:~	~Dividend Yield~	~Distributor~	~Display.Form.Login.rowCount~	~Display.Form.Login.p~	~Display.Form.Login.newrowCount~	~Display.Form.Login~	~Display.Form.AccountService.serviceStatusID~	~Display.Form.AccountService.rowCount~	~Display.Form.AccountService.newrowCount~	~Display.Form.AccountNew.rowCount~	~Display.Form.AccountNew.PASSWORD~	~Display.Form.AccountNew.newrowCount~	~Display.Form.AccountNew.idValue~	~Display.Form.AccountNew.c~	~Display.Form.AccountNew.BIRTHDATE~	~Display.Form.AccountNew.b~	~Display.Form.AccountBilling.rowCount~	~Display.Form.AccountBilling.newrowCount~	~Display.Form.AccountBilling.a~	~Display.Form.Account~	~Display View~	~Display info for:~	~Display Display up to~	~display~	~dispatch~	~Disk #:~	~discoveryOther~	~Direct flights only~	~Dewey decimal:~	~Development Tools~	~Details~	~DestinationUrl~	~Destination: List of Cities List of Regions~	~Destination#1_DIRECT_NON_STOP~	~Destination#1_Airline#3~	~Destination#1_Airline#2~	~Destination#1_Airline#1~	~destination~	~dest~	~Desired Handle User ID:~	~Description:*( Help (Note: Loan Term may vary.)~	~Description~	~Describe Who You'd Like To Meet:~	~DesapprovUrl~	~depRetDates~	~departureYear~	~departureTEXT~	~departureMonth~	~departureDay~	~Departure: (M D YYYY)~	~Departure Date: second~	~Departure Date: minute~	~Departure Date: hour~	~Departure Date: am pm~	~Departure Date:~	~Departure date year~	~Departure date second~	~Departure date month~	~Departure date minute~	~Departure date hour~	~Departure date day~	~Departure date am pm~	~Departure date~	~Departure ֳ‚ֲ�ֳ‚ֲ�date:~	~departTime~	~Department:~	~Department Mail Stop~	~Department~	~Departing~	~Depart: second~	~Depart: minute~	~Depart: hour~	~Depart: am pm~	~Depart:~	~Depart second~	~Depart minute~	~Depart hour~	~Depart am pm~	~Depart (MM DD YYYY):~	~Depart~	~deparr value=~	~dep_dt_yr_2~	~dep_dt_yr_1~	~Defense Aerospace Military~	~Debt Equity Ratio~	~db~	~Daytime Phone:~	~Daytime Phone Number:~	~Daytime phone number~	~Daytime Phone~	~Day time telephone number~	~Day Phone:~	~dating.com news~	~dateFormat~	~dateFields~	~Date:~	~Date.com - Join Now for Free!~	~Date Range:~	~Date of Purchase:~	~Date of Incident~	~Date of birth:~	~Date of Birth (mm dd yyyy)~	~date of birth (dd mm yyyy):~	~Date Of Birth (DD MM YYYY) :~	~Date of Birth~	~Date of Article (select one) To select more than one year, hold down the Command key (Mac) or Control key (Win) while selecting.~	~Date observed~	~Date format: The syntax used is identical to the PHP date() function.~	~date~	~Data port~	~Darryl Worley~	~D_Time~	~CustServForm~	~Customer Service~	~cust_id~	~Cust~	~currentCodeForm~	~currentCalForm~	~Current Quarter Estimate~	~Current Position~	~Current Employer:~	~Current Employer~	~currency_id~	~Currency: *~	~Currency:~	~curpage~	~cur~	~ctlLoanQuickloan1:ctlDOB:txtShowPastYears~	~ctlLoanQuickloan1:ctlDOB:txtShowFutureYears~	~ctlLoanQuickloan1:CtlCodatepicker:txtShowPastYears~	~ctlLoanQuickloan1:CtlCodatepicker:txtShowFutureYears~	~ct~	~CS_Browser_Timestamp_53663_53679~	~CS~	~Credit Rating~	~Credit Card: Card Number: Valid:~	~Credit Card:~	~Credit card type~	~Credit Card Number:~	~Credit card number~	~CreateNewUserForm~	~Create Password:~	~Create Cashette ID:~	~CREATE ACCOUNT~	~Create a Username:~	~CQMARK~	~CQ_SESSION_KEY~	~CQ_SAVE[Start_Row]~	~CQ_DTF_QUICKSEARCH_RESULTS~	~Course Name:~	~County:~	~County State~	~County District: *~	~County District:~	~County~	~country_code~	~Country: *~	~Country: (*Required)~	~Country:~	~Country Region:~	~Country :~	~Country *~	~Country (required)~	~Country (Non-U.S.)~	~country~	~coreg~	~Copyright:~	~coppa~	~Cooking~	~ControlRegistrationLoginContainer:ControlRegistrationContainer:ControlRegistration:stateHistory~	~ControlRegistrationLoginContainer:ControlRegistrationContainer:ControlRegistration:countryHistory~	~controlid~	~Continue~	~Continental Airlines - Airline Tickets, Vacations Packages, Travel Deals, and Company Information on continental.com~	~Contact United | Site search :~	~Contact Phone: (xxx-xxx-xxxx) Help~	~Contact Person: *~	~Contact Name:~	~Contact Fax Number:~	~Consumer Products~	~Consulting Services~	~Consolidation Loan Application~	~ConfShown~	~confr~	~Confirmֳ‚ֲ�ֳ‚ֲ� Password:~	~Confirm:~	~Confirm password: *~	~Confirm password:~	~Confirm Password~	~Confirm Email: *~	~Confirm Email:~	~Confirm Email Address~	~Conferencing Products Services~	~Computers Technology~	~Computers & Technology~	~Computer Peripherals~	~Computer & Electronics~	~complaintform~	~Complaint Information: ( who, what, when, where, why & how )~	~Complaint [250 characters or less]~	~complaint~	~Company:~	~Company URL:~	~Company Phone: (If different than above)~	~Company Name:~	~Company Name~	~Company Fax: (If different than above)~	~Company Address: (If different than above)~	~Company~	~Communications Servers~	~Communications~	~Comments:~	~commandToExecute~	~Color~	~College+~	~collection~	~col~	~cof~	~Code of airline:~	~cms_request~	~cmd~	~Close~	~client~	~Click here to submit request~	~Click here for definitions of some of these business types .) Distributor Stationer Printer Selling Forms Direct Manufacturer Supplier to Trade Educational Other (provide description):~	~clear_cache~	~Clear Form~	~Clear All~	~Clear~	~Cldid~	~classified in one of the following Dow Jones Industry groups:~	~classID~	~Class:~	~Class of Service:~	~Class~	~City_C~	~City: *~	~City:~	~City Zone:~	~City Zone Place:~	~City Town :~	~City Town~	~City Suburb:~	~city name:~	~City Locality: *~	~City Locality:~	~City :~	~City *~	~City (compulsory)~	~City~	~CID~	~Choose your User Name~	~Choose the type of search you would like to perform:~	~Choose Shipping Method:~	~Choose month~	~Choose hotel:~	~Choose a username:~	~Choose a username~	~Choose a password:~	~Choose a password~	~chkTransfer~	~chkFltOptyfare~	~chkFltOptopupgrade~	~chkFltOptoppromo~	~chkFltOptnpe~	~chkFltOptnap~	~chkBonus~	~Children:~	~Children 2-11 yrs~	~Children (2-11 yrs) Infants (0-2 yrs)~	~Children (2-11 yrs)~	~Children~	~child1~	~Child:~	~Child(ren)~	~Child (2-12 yrs)~	~chgcntry~	~Check-out date:~	~Check-out~	~checkout~	~Check-In:~	~Check-in date:~	~Check-in~	~checkboxfields~	~CheckBox_Defaults~	~Check this box to be included in our singles-only area.~	~Check rates and availability when searching:~	~Check Out~	~Check in:~	~Check In~	~Check if billing and shipping addresses are the same.~	~Check Availability~	~Check~	~Cheap rates on Hotels - Hotels in - Hotel List~	~charset~	~Characters:~	~channel~	~Change the style of your search:~	~Change the mode of your search:~	~Challenge Question~	~Challenge Answer~	~Chain:~	~CFForm_1~	~CEO President Chairman VP CIO IT IS Network Telecom PC Director Manager Programmer Engineer Analyst Developer Other IT or Telecom Staff Sales Marketing Accounting Office Admin Finance Other Department Staff Consultant Other~	~Cellular phone (Hands free)~	~CellPhone~	~cellEmail~	~Cell Phone Number~	~Cell Phone Carrier~	~Cell Phone~	~cds_page_id~	~cds_mag_code~	~cds_country~	~cboSenior65~	~cboSenior~	~cboPrevCountry~	~cboNumOfFlts1~	~cboInfantLap~	~cboInfant~	~cboCurrency~	~cboCountry~	~cboChildTwo~	~cboChildTwelve~	~cboChildFive~	~catPath~	~catId~	~Category:~	~Category~	~CASINO_URL~	~CASINO_NAME~	~Casino Search:~	~Casino Promotion Code:~	~Casino Name:~	~Cash Flow Growth 1 Yr~	~CartId~	~Career & Education~	~Cardholder's Name:~	~Card Validation Code*~	~Card Type:~	~Card Number:~	~Card number~	~Card no.~	~Card Holder Name~	~Card expire date~	~Car Type Class~	~Car group:~	~cancel_uri~	~Cancel~	~campaign_email_forceopt-4103~	~campaign~	~CALLINGURL~	~Call Center Systems and Software~	~CalcForm~	~CAFunding~	~Cabin:~	~c~	~By checking the box you agree to the MySpace~	~BV_SessionID~	~BV_EngineID~	~Business Phone:~	~Business Phone Systems~	~Business Opportunities~	~Business Center~	~Business~	~Budget Israel~	~Browse Content~	~Broadband Services~	~botForm~	~BookSetSearch~	~bookset~	~Books Reading~	~bookingPostVerify~	~Booking Class~	~book2~	~Book or Subscription Title~	~Book Class~	~Bonus_scheme_value~	~bonk~	~Body type:~	~Board Style:~	~Board Language:~	~Blades~	~Black African-American~	~Birthday: *~	~Birthday:~	~Birthday (mm-dd-yyyy)~	~Birthday~	~Birthdate (required)~	~Birth-Date (MMDDYYYY):~	~Birthdate~	~Birth month *~	~Birth date:~	~Birth Date~	~Bio Medical~	~Binding Type~	~Billy Currington~	~Billing Address (P.O. Box OK) City State Province Zip Postal Code~	~Billing Address (P.O. Box OK) City State Province~	~Billing Address (P.O. Box OK) City~	~Billing Address (P.O. Box OK)~	~Bill to Address:~	~bil~	~BID~	~BFINFO~	~BETWEEN~	~betting currency:~	~Betting Currency~	~Beta (Volatility):~	~Best Tel# To Call~	~Best call time?~	~Beds:~	~Bed Type:~	~Become a Member Now!~	~BE_QUICK_SEARCH_FLAG~	~BE_AIR_R_Time~	~BE_AIR_IS_FROM_SAVED~	~basketForm~	~basket~	~basicSearchForm~	~Barrow Reference:~	~Bank use only:~	~Bank Routing Number~	~Bank Phone~	~Bank Name~	~BackUrl~	~Backplanes Enclosures~	~Back To Sportsbook Home~	~Baby seat (on request)~	~b_sourcecode~	~b_prospectid~	~b_accountnum~	~Avg Analyst Rec: (1=Buy, 5=Sell)~	~Average Volume~	~Average Customer Rating~	~availSubmit~	~availSearchSubmit~	~Availability.x~	~avail~	~Autos~	~Automotive~	~AUTOEUROPE CAR RESERVATION~	~Auto~	~Author:~	~Author~	~audience~	~Association Manager subscription form~	~aspnetForm~	~Asian or Pacific Islander~	~articleid~	~articleaction~	~ArrText~	~arrivalTEXT~	~arrivalMonth~	~arrivalDay~	~Arrival: (M D YYYY)~	~Arrival Date:~	~ARRANGE_BY~	~Area Code + Phone:~	~Area Code~	~Are you paid by Direct Deposit~	~Are you an SAE member? Yes No SAE Member Number:~	~Are you an SAE member?~	~Are you a member of the Free World DialUp?~	~arabia.com offers you a second email domain without having to register again. You can use the same username and password to access it and it will give you a further 10MB of storage space absolutely free! Please select your second domain from the following list: ( Optional )~	~arabia.com - Registration~	~Apt. #:~	~Apt, Unit, or Suite Number:~	~Apt #:~	~ApplyAccCheck~	~apply~	~Applications Engineer~	~appform~	~Apartment #~	~Anytime~	~Anything else?:~	~Any special skills you have (languages or further education qualifications)~	~any of these words~	~Any additional markings on vehicle such as business name, phone number, permit numbers, etc. (You may keep typing beyond the boundary of the box. Your entire entry will be forwarded with this message.)~	~answerstr~	~Answer:*~	~Answer:~	~Annual Sales Volume:~	~Annual Income Range (US$):~	~Annotation:~	~and show~	~and on or before~	~and~	~Analysts Buy Hold Sell Mean~	~Analyst Consensus~	~Amount of Loan: Help~	~Amount in Dispute:~	~American Indian Aleut or Eskimo I'd rather not say~	~American Indian Aleut or Eskimo~	~AMENITY_Tennis~	~AMENITY_Snow Skiing~	~AMENITY_Shops/Commercial Services~	~AMENITY_Safe Deposit Box~	~AMENITY_Rooms for the Disabled~	~AMENITY_Room Service~	~AMENITY_Restaurant~	~AMENITY_Pool~	~AMENITY_Pets Allowed~	~AMENITY_Meeting/Banquet Facilities~	~AMENITY_Laundry/Valet Services~	~AMENITY_Golf~	~AMENITY_Free Parking~	~AMENITY_Free Newspaper~	~AMENITY_Fitness Center or Spa~	~AMENITY_Express Checkout~	~AMENITY_Concierge~	~AMENITY_Casino~	~AMENITY_Business Center~	~AMENITY_Beach~	~AMENITY_Barber/Beauty Shop~	~AMENITY_Bar/Lounge~	~AMENITY_Babysitting/Child Services~	~AMENITY_24 Hour Front Desk~	~Always show my e-mail address:~	~Always notify me of replies: Sends an e-mail when someone replies to a topic you have posted in. This can be changed whenever you post.~	~Always enable Smilies:~	~Always attach my signature:~	~Always allow HTML:~	~Always allow BBCode:~	~Alternative address:*~	~Alternate Return Date~	~Alternate e-mail address:~	~Alternate Email~	~Alternate Departure Date~	~Also known as the multiple, this is the latest closing price divided by the latest 12 months' earnings per share~	~Allow others to see when it's my birthday~	~all of these words~	~All NYSE NASDAQ AMEX~	~aktie~	~Airport Shuttle~	~Airline:~	~airline~	~airItinRT~	~Aircraft Type:~	~air_avail~	~AIM Address:~	~agreed~	~agentGroup~	~agent_id~	~agent~	~Age Range:~	~Age range from~	~AffBetslip~	~aff~	~aerodynURL~	~adverid~	~Adventure Outdoors~	~advancedsearch~	~adv~	~Adults:~	~Adults~	~adult1~	~Adult:~	~Adult(s) (12+ yrs)~	~Adult(s)~	~Adult 12+ yrs Children 2-11 yrs Infants 0-1 yrs~	~Adult 12+ yrs~	~Adult (12 yrs and up)~	~Adres of cardholder~	~Administrative~	~adForm~	~addToList~	~addtobasket~	~addressForm~	~Address2:~	~Address2~	~Address1_C~	~Address1:~	~Address1 *~	~Address: *~	~Address:~	~Address, line 2:~	~Address, line 1:~	~Address second line:~	~Address Line 2~	~Address Line 1~	~Address first line:~	~Address cont.:~	~Address 2:~	~Address 2~	~Address 1 (*)~	~Address ";
		String val="~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~3~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~	~1~";



		String attribute_name="";
		String attribute_value="";
		Vector<String>terms = new Vector<String>();
		Vector<Integer>value=new Vector<Integer>();
		int flag=0;
		for (int i=0,n=all_attributes.length();i<n;i++)
		{
			if(all_attributes.charAt(i)=='~')
			{
				return Web_to_array( web_form,val,proportion);
			}

			while(Character.isWhitespace(all_attributes.charAt(i)) == false)
			{
				if(flag==0)
				{
					if(all_attributes.charAt(i)!=',')
						attribute_name+=all_attributes.charAt(i);
				}
				if(flag==1)
				{
					attribute_value+=all_attributes.charAt(i);
				}
				if(all_attributes.charAt(i)==',')
				{
					flag=1;
				}
				break;

			}
			if(Character.isWhitespace(all_attributes.charAt(i)) == true)
			{
				terms.addElement(attribute_name);
				value.addElement(Integer.parseInt(attribute_value));
				attribute_value="";
				attribute_name="";
				flag=0;


			}

		}
		int total=0,sum=0;
		String a[]=new String[value.size()];
		for (int i=0,n=value.size();i<n;i++)
		{
			total+=value.get(i);
		}
		total*=proportion;
		int i=0;
		while(total>sum)
		{

			sum+=value.get(i);
			a[i]=terms.get(i);
			i++;

		}
		String a1[]=new String[i];
		for(int i1=0;i1<i;i1++)
			a1[i1]=a[i1];
		return a1;
	}

	//create edges
	public void create_edges(Set<Set<String>> edges,Set<Set<String>> deleted_edges,Set<String> Dictionary_UAF,List<String> cTerms_array,List<String> tTerms_array)
	{
		//creating edges
		for (String i : Dictionary_UAF){
			for (String j : Dictionary_UAF){
				if (i!=j) {
					if ((cTerms_array.contains(i) && cTerms_array.contains(j)) || (tTerms_array.contains(i) && tTerms_array.contains(j)) )
					{
						Set<String> tmpSet = new HashSet<String>();
						tmpSet.add(i);
						tmpSet.add(j);
						deleted_edges.add(tmpSet);}
					else
					{
						Set<String> tmpSet = new HashSet<String>();
						tmpSet.add(i);
						tmpSet.add(j);
						edges.add(tmpSet);
					}
				}

			}
		}
	}

	//create cliques
	public void create_cliques(Set<Set<String>> edges,Set<Set<String>> deleted_edges,Set<String> Dictionary_UAF,Set<Set<String>> cliques)
	{
		Set<String> finding_cliques;
		for (String i : Dictionary_UAF)
		{
			finding_cliques = new HashSet<String>();
			Iterator<Set <String>> iterator =edges.iterator();
			while(iterator.hasNext() ) {
				Set <String> sub_set= iterator.next();
				if (sub_set.contains(i))
				{
					Iterator<String> iterator2 =sub_set.iterator();
					while(iterator2.hasNext() ) {
						String term=iterator2.next();
						finding_cliques.add(term);
					}
				}
			}
			cliques.add(finding_cliques);
		}
		int counter=0;
		Set<Set<String>> cliques_tmp = new HashSet<Set<String>>(cliques);
		Iterator<Set <String>> iterator =cliques_tmp.iterator();
		Iterator<Set <String>> iterator_edges =deleted_edges.iterator();
		while(iterator_edges.hasNext() ) {
			Set<String> edges_sub_set = iterator_edges.next();
			while (iterator.hasNext()) {
				Set<String> sub_set = iterator.next();
				if (sub_set.containsAll(edges_sub_set)) {
					cliques.remove(sub_set);
				}
			}

		}

	}

	void build_set(Vector<Term> cTerms,Vector<Term> tTerms,Set<String>c_set,Set<String>t_set)
	{
		for(int i=0,n=cTerms.size();i<n;i++)
		{
			c_set.add(cTerms.get(i).getName());
		}
		for(int i=0,n=tTerms.size();i<n;i++)
		{
			t_set.add(tTerms.get(i).getName());
		}
	}

	public double []  beta_prob(Set< Set< String > > collection,Map<String,Double>beta,Set<String>c_set,Set<String>t_set,Map<Set< String >,Double>alpha)
	{
		Iterator<Set< String >> iterator = collection.iterator();
		double  denominator=0;

		while(iterator.hasNext())
		{	denominator=0;
			int index=0;
			Set<String> sub_set=iterator.next();
			Iterator<String>iter=sub_set.iterator();
			while(iter.hasNext())
			{
				String attr=iter.next();
				double counter=0;
				if(c_set.contains(attr))
					counter++;
				if(t_set.contains(attr))
					counter++;
				if(counter==0)
					counter=0;
				beta.put(attr, counter);
				index+=counter;

			}
			if(index==0)
			{
				iter=sub_set.iterator();
				while(iter.hasNext())
					beta.put(iter.next(), 0.0);
			}
			else
			{
				iter=sub_set.iterator();
				while(iter.hasNext())
				{
					String s =iter.next();
					beta.put(s, beta.get(s)/index);
				}
			}
			alpha.put(sub_set, (double)index/2);

		}

		Iterator<String>iter=c_set.iterator();
		double scema_prob=1,alpha_beta=1;
		Set<Set<String>>visited=new HashSet<Set<String>>();
		Set<String>sub_set=new HashSet<String>();
		String attr="";
		while(iter.hasNext())
		{
			iterator=collection.iterator();
			attr=iter.next();
			int count=0;
			while(iterator.hasNext())
			{
				sub_set=iterator.next();
				if(sub_set.contains(attr))
				{
					count++;
					alpha_beta*=beta.get(attr)*alpha.get(sub_set);
					visited.add(sub_set);
				}

			}
			if(count==0)
			{
				alpha_beta*=1;
			}


		}

		Set< Set< String > > collection1=new HashSet<Set<String>>();
		collection1.addAll(collection);
		collection1.removeAll(visited);

		double [] prob=new double[2];
		if(collection1.isEmpty())
			prob[0]=alpha_beta;
		else
		{
			iterator = collection1.iterator();
			while(iterator.hasNext())
			{
				scema_prob*=1-(alpha.get(iterator.next()));
			}

			prob[0]=alpha_beta*scema_prob;
		}

		iter=t_set.iterator();
		scema_prob=1;
		alpha_beta=1;
		visited=new HashSet<Set<String>>();
		sub_set=new HashSet<String>();
		attr="";
		while(iter.hasNext())
		{
			iterator=collection.iterator();
			attr=iter.next();
			int count=0;
			while(iterator.hasNext())
			{
				sub_set=iterator.next();
				if(sub_set.contains(attr))
				{
					count++;
					alpha_beta*=beta.get(attr)*alpha.get(sub_set);
					visited.add(sub_set);
				}

			}
			if(count==0)
			{
				alpha_beta*=1;
			}


		}
		collection1=collection;
		collection1.removeAll(visited);

		if(collection1.isEmpty())
			prob[1]=alpha_beta;
		else
		{
			iterator = collection1.iterator();
			while(iterator.hasNext())
			{
				scema_prob*=1-(alpha.get(iterator.next()));
			}

			prob[1]=alpha_beta*scema_prob;
		}


		double d=0;
		d=Math.pow(1-2*prob[0], 2)/(2*prob[0]);
		d+=Math.pow(2-2*prob[1], 2)/(2*prob[1]);

		return prob;
	}

	public Set<Set<String>> optimal_model(Set<String>c_set,Set<String>t_set,List<String> Dictionary_UAF,Set<Set<String>> cliques,Map<Double,Set<Set<String>>> optimal_model ) {

		Set<Set<String>> minimal_cover_model = new HashSet<Set<String>>();
		Random n = new Random();

		for (int i = 0; i < 1; i++) {

			minimal_cover_model.clear();
			Iterator<Set<String>> iterator_cliques = cliques.iterator();
			Set<String> added_edges = new HashSet<String>();
			while (iterator_cliques.hasNext()) {
				Set<String> edges_sub_set = iterator_cliques.next();
				Set<String> tmp_edges_sub_set = new HashSet<String>(edges_sub_set);
				if (added_edges.size() == Dictionary_UAF.size()) {
					added_edges.clear();
					break;
				}
				tmp_edges_sub_set.removeAll(added_edges);
				int size = tmp_edges_sub_set.size();
				while (size > 0) {
					Set<String> tmpset = new HashSet<String>();

					int  randomNum =( n.nextInt(tmp_edges_sub_set.size()) +0)/20;

					List<String> tmplist = new ArrayList<String>(tmp_edges_sub_set);
					for (int j = 0; j <= randomNum; j++) {

						int rand = ThreadLocalRandom.current().nextInt(0, tmp_edges_sub_set.size());

						if (added_edges.contains(tmplist.toArray()[rand].toString())) {
							if (randomNum < tmp_edges_sub_set.size() - 2)
								randomNum = randomNum + 1;
							tmp_edges_sub_set.remove(tmplist.toArray()[rand].toString());
							tmplist = new ArrayList<String>(tmp_edges_sub_set);
						} else {

							tmpset.add(tmplist.toArray()[rand].toString());
							added_edges.add(tmplist.toArray()[rand].toString());
						}
						size = tmp_edges_sub_set.size();
					}
					if (tmp_edges_sub_set.size() > 0 && tmpset.size() > 0) {
						minimal_cover_model.add(tmpset);
						tmp_edges_sub_set.removeAll(tmpset);
						size = tmp_edges_sub_set.size();
					}
				}

			}
			Set<Set<String>> tmp_minimal_cover_model = new HashSet<Set<String>>(minimal_cover_model);
			Set<Double> prob_given_i = new HashSet<Double>();
			double [] prob=new double[2];
			Map<String,Double>beta=new HashMap<String, Double>();
			Map<Set< String >,Double>alpha=new HashMap<Set< String >, Double>();
			double[] d=beta_prob(tmp_minimal_cover_model, beta, c_set, t_set, alpha);
			optimal_model.put(d[0]*d[1], tmp_minimal_cover_model);
		}

		return minimal_cover_model;
	}

	public final double distance(final String s1, final String s2)
	{
		if (s1 == null) {
			throw new NullPointerException("s1 must not be null");
		}

		if (s2 == null) {
			throw new NullPointerException("s2 must not be null");
		}

		if (s1.equals(s2)) {
			return 0;
		}

		if (s1.length() == 0) {
			return s2.length();
		}

		if (s2.length() == 0) {
			return s1.length();
		}

		// create two work vectors of integer distances
		int[] v0 = new int[s2.length() + 1];
		int[] v1 = new int[s2.length() + 1];
		int[] vtemp;

		// initialize v0 (the previous row of distances)
		// this row is A[0][i]: edit distance for an empty s
		// the distance is just the number of characters to delete from t
		for (int i = 0; i < v0.length; i++) {
			v0[i] = i;
		}

		for (int i = 0; i < s1.length(); i++) {
			// calculate v1 (current row distances) from the previous row v0
			// first element of v1 is A[i+1][0]
			//   edit distance is delete (i+1) chars from s to match empty t
			v1[0] = i + 1;

			// use formula to fill in the rest of the row
			for (int j = 0; j < s2.length(); j++) {
				int cost = 1;
				if (s1.charAt(i) == s2.charAt(j)) {
					cost = 0;
				}
				v1[j + 1] = Math.min(
						v1[j] + 1,              // Cost of insertion
						Math.min(
								v0[j + 1] + 1,  // Cost of remove
								v0[j] + cost)); // Cost of substitution
			}

			// copy v1 (current row) to v0 (previous row) for next iteration
			//System.arraycopy(v1, 0, v0, 0, v0.length);

			// Flip references to current and previous row
			vtemp = v0;
			v0 = v1;
			v1 = vtemp;

		}

		return v0[s2.length()];
	}

	public double min_distance(String cTerm ,Set<Set<String>> optimal_model)
	{
		double temp=0;
		String term;
		double dist;

		Iterator<Set<String>> iterator_cliques = optimal_model.iterator();
		while (iterator_cliques.hasNext()) {
			Set<String> edges_sub_set = iterator_cliques.next();
			Iterator<String> iterator_edges_sub_set = edges_sub_set.iterator();
			term=iterator_edges_sub_set.next();
			temp=distance(term,cTerm)/Math.max(term.length(),cTerm.length());
			while (iterator_edges_sub_set.hasNext()) {
				term=iterator_edges_sub_set.next();
				dist=distance(term,cTerm)/Math.max(term.length(),cTerm.length());
				if (dist<temp) temp=dist;
			}
		}
		return 1-temp;
	}

	public MatchInformation match(Ontology candidate, Ontology target, boolean binary)
	{
		Random n = new Random();
		MatchInformation res = new MatchInformation(candidate, target);
		Map<Double,Set<Set<String>>> optimal_model=new HashMap< Double,Set<Set<String>>>();
		Vector<Term> cTerms = candidate.getTerms(true);
		Vector<Term> tTerms = target.getTerms(true);

		for (int pair=0;pair<200;pair+=2) {

			cTerms = candidate.getTerms(true);
			tTerms = target.getTerms(true);

			int  randomNum_cTerms =n.nextInt( cTerms.size());
			int  randomNum_tTerms =n.nextInt( tTerms.size());

			for (int j = 0; j < randomNum_cTerms; j++) {cTerms.removeElementAt(n.nextInt( cTerms.size()));}
			for (int j = 0; j < randomNum_tTerms; j++) {tTerms.removeElementAt(n.nextInt( tTerms.size()));}

			List<String> Dictionary_UAF = new ArrayList<String>(Arrays.asList(return_model(0.3)));
			List<String> cTerms_array = new ArrayList<String>();
			List<String> tTerms_array = new ArrayList<String>();
			Set<Set<String>> cliques = new HashSet<Set<String>>();
			Set<Set<String>> edges = new HashSet<Set<String>>();
			Set<Set<String>> deleted_edges = new HashSet<Set<String>>();
			Set<String> c_set = new HashSet<String>();
			Set<String> t_set = new HashSet<String>();

			build_set(cTerms, tTerms, c_set, t_set);

			//creating array of terms shcema 1
			for (int i = 0; i < cTerms.size(); i++) {
				cTerms_array.add(cTerms.get(i).getName());
			}
			//creating array of terms shcema 2
			for (int i = 0; i < tTerms.size(); i++) {
				cTerms_array.add(tTerms.get(i).getName());
			}

			Set<String> Dictionary_UAF_set = new HashSet<String>(Dictionary_UAF);

			create_edges(edges, deleted_edges, Dictionary_UAF_set, cTerms_array, tTerms_array);
			create_cliques(edges, deleted_edges, Dictionary_UAF_set, cliques);
			optimal_model(c_set, t_set, Dictionary_UAF, cliques,optimal_model);

		}
		double temp=0;
		double temp2;
		cTerms = candidate.getTerms(true);
		tTerms = target.getTerms(true);

		Iterator <Double> optimal_model_iter = optimal_model.keySet().iterator();
		while (optimal_model_iter.hasNext()) {
			temp2=optimal_model_iter.next();
			if (temp2>temp) temp=temp2;
		}
		Map<String,Double> cTerms_model=new HashMap<String,Double>();
		Map<String,Double> tTerms_model=new HashMap<String,Double>();

		for (int i=0; i<cTerms.size();i++) {
			cTerms_model.put(cTerms.get(i).getName(),min_distance(cTerms.get(i).getName(),optimal_model.get(temp)));
		}
		for (int j=0; j<tTerms.size();j++) {
			tTerms_model.put(tTerms.get(j).getName(),min_distance(tTerms.get(j).getName(),optimal_model.get(temp)));
		}

		Map<String,Set<String>> optimal_model_map=new HashMap<String,Set<String>>();
		Iterator <Set<String>> optimal_iter = (optimal_model.get(temp)).iterator();

		while (optimal_iter.hasNext()) {
			Set<String> temp_iter=optimal_iter.next();
			Iterator <String> temp_iter2 = temp_iter.iterator();
			while (temp_iter2.hasNext()) {optimal_model_map.put(temp_iter2.next(),temp_iter);}
		}

		for (int i=0; i<cTerms.size(); i++)
		{
			for (int j=0; j<tTerms.size(); j++)
			{
				if (tTerms.get(j).getName()!=cTerms.get(i).getName())
				{
					if (optimal_model_map.containsKey(cTerms.get(i).getName()))
					{
						if (optimal_model_map.get(cTerms.get(i).getName()).contains(tTerms.get(j).getName()))
							res.updateMatch	(tTerms.get(j), cTerms.get(i), 1.0);
						else if (optimal_model_map.containsKey(tTerms.get(j).getName()))
							res.updateMatch	(tTerms.get(j), cTerms.get(i), 0.0);
						else res.updateMatch(tTerms.get(j), cTerms.get(i),cTerms_model.get(cTerms.get(i).getName()));									}
					else if (optimal_model_map.containsKey(tTerms.get(j).getName()))
					{
						if (optimal_model_map.get(tTerms.get(j).getName()).contains(cTerms.get(i).getName()))
							res.updateMatch	(tTerms.get(j), cTerms.get(i), 1.0);
						else if (optimal_model_map.containsKey(cTerms.get(i).getName()))
							res.updateMatch	(tTerms.get(j), cTerms.get(i), 0.0);
						else res.updateMatch(tTerms.get(j), cTerms.get(i),tTerms_model.get(tTerms.get(j).getName()));									}
					else
						res.updateMatch(tTerms.get(j), cTerms.get(i),tTerms_model.get(tTerms.get(j).getName())*cTerms_model.get(cTerms.get(i).getName()));
				}
			}
		}
		candidate.removeTerm(candidate.getTerm(0));//new addition by Roee
		return res;
	}


	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getConfig()
	 */
	@Override
	public String getConfig() {
		return "no configurable parameters";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getType()
	 */
	@Override
	public MatcherType getType() {
		return MatcherType.STRUCTURAL_SIBLING;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getDBid()
	 */
	@Override
	public int getDBid() {
		return 29;
	}

}
