package deconvolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CommandLineOptions {
	private String expressionFile;
	private String genotypeFile;
	private String cellcountFile;
	private String snpsToTestFile;
	private String outfile = "deconvolutionResults.csv";
	private String outfolder;
	private int numberOfPermutations = 0;
	private String permutationType = "genotype";
	private Boolean forceNormalExpression = false;
	private Boolean forceNormalCellcount = false;
	private int minimumSamplesPerGenotype = 0;
	private Boolean roundDosage = false;
	private Boolean allDosages = false;
	private String normalizationType = "normalizeAddMean";
	private Boolean filterSamples = false;
	private Boolean removeConstraintViolatingSamples = false;
	private Boolean onlyOutputSignificant = false;
	private Boolean useRelativeCellCounts = false;
	private String validate;
	private Boolean testRun = false;
	private String multipleTestCorrectionMethod = "bonferonni";
	private Boolean skipGenotypes = false;
	private Boolean wholeBloodQTL = false;
	private Boolean noConsole = false;
	/**
	 * Standard command line parsing.
	 * 
	 * @param args A string vector of all arguments given to the command
	 * line, e.g. for `java -jar Deconvolution.jar -o` args = ["-o"]
	 * 
	 * @return A CommandLine object that includes all the options given to
	 * the command line
	 */
	public void parseCommandLine(String[] args) throws ParseException, FileNotFoundException {
		Options options = new Options();
		Option help = new Option("help", "print this message");
		Option allDosages = Option.builder("ad").required(false).longOpt("all_dosages")
				.desc("Filter out QTLs where not all dosages are present in at least 1 sample").build();
		Option cellcount = Option.builder("c").required(true).hasArg().longOpt("cellcount").desc("Cellcount file name")
				.argName("file").build();
		Option useRelativeCellCountsOption = Option.builder("cc").required(false).longOpt("use_relative_cellcounts")
				.desc("Calculate ratio between cellcount and cellcount average, use that as cellcount in the model").build();
		Option expression = Option.builder("e").required(true).hasArg().longOpt("expression")
				.desc("Expression file name").argName("file").build();
		Option filterSamplesOption =  Option.builder("f").required(false).longOpt("filter_samples")
				.desc("If set, remove samples that are filtered out because of -m, -nn or -ad. By default p-values of these are set to 333.0").build();
		Option genotype = Option.builder("g").required(true).hasArg().longOpt("genotype").desc("Genotype file name")
				.argName("file").build();
		Option minimumSamplesPerGenotype = Option.builder("m").required(false).hasArg().longOpt("minimum_samples_per_genotype")
				.desc("The minimum amount of samples need for each genotype of a QTL for the QTL to be included in the results")
				.argName("int").build();
		Option multipleTestCorrectionMethod = Option.builder("mt").required(false).hasArg().longOpt("multiple_testing_correction_method")
				.desc("Method used for doing multiple testing correction (currently only bonferonni)").build();
		Option normalizationType = Option.builder("n").required(false).hasArg().longOpt("normalization_type")
				.desc("Type to normalization to use when normalizing expression data (Default: normalizeAddMean)").build();
		Option forceNormalCellcount = Option.builder("nc").required(false).hasArg().longOpt("force_normal_cellcount")
				.desc("Force normal on the expression data").build();
		Option forceNormalExpression = Option.builder("ne").required(false).longOpt("force_normal_expression")
				.desc("Force normal on the expression data").build();
		Option noConsoleOption = Option.builder("no").required(false).longOpt("no_console")
				.desc("Do not output logging info to the console").build();
		Option outfolder = Option.builder("o").required(true).hasArg().longOpt("outfolder").desc("Path to folder to write output to")
				.argName("path").build();
		Option outfile = Option.builder("of").required(false).hasArg().longOpt("outfile").desc("Outfile name of deconvolution results (will be written in outfolder)")
				.argName("file").build();
		Option permute = Option.builder("p").required(false).longOpt("permute").hasArg()
				.desc("Do permutations. Uses more than usual memory.").build();
		Option permuteType = Option.builder("pt").required(false).hasArg().longOpt("permutation_type")
				.desc("Type to permute on, either genotype or expression (Default: genotype)").build();
		Option roundDosage = Option.builder("r").required(false).longOpt("round_dosage")
				.desc("Round the dosage to the closest int").build();
		Option onlyOutputSignificantOption = Option.builder("s").required(false).longOpt("output_significant_only")
				.desc("Only output results that are significant in at least one celltype.").build();
		Option skipGenotypes = Option.builder("sg").required(false).longOpt("skip_genotypes")
				.desc("Skip genotypes that are in the GeneSNP pair file but not in the genotype file.").build();
		Option snpsToTestOption = Option.builder("sn").required(true).hasArg().longOpt("snpsToTest").argName("file")
				.desc("Tab delimited file with first column gene name, second column SNP name. Need to match with names from genotype and expression files.").build();
		Option doTestRun = Option.builder("t").required(false).longOpt("test_run")
				.desc("Only run deconvolution for 100 QTLs for quick test run").build();
		Option validateResults = Option.builder("v").required(false).hasArg().longOpt("validate_output")
				.desc("Validate how well deconvolution worked by comparing to cell-type specific data").build();
		Option wholeBloodQTL = Option.builder("w").required(false).longOpt("whole_blood_qtl")
				.desc("Add whole blood eQTL (pearson correlation genotypes and expression)").build();
		options.addOption(onlyOutputSignificantOption);
		options.addOption(filterSamplesOption);
		options.addOption(normalizationType);
		options.addOption(help);
		options.addOption(permute);
		options.addOption(outfile);
		options.addOption(expression);
		options.addOption(genotype);
		options.addOption(cellcount);
		options.addOption(roundDosage);
		options.addOption(minimumSamplesPerGenotype);
		options.addOption(forceNormalExpression);
		options.addOption(forceNormalCellcount);
		options.addOption(permuteType);
		options.addOption(allDosages);
		options.addOption(outfolder);
		options.addOption(useRelativeCellCountsOption);
		options.addOption(validateResults);
		options.addOption(doTestRun);
		options.addOption(multipleTestCorrectionMethod);
		options.addOption(snpsToTestOption);
		options.addOption(skipGenotypes);
		options.addOption(wholeBloodQTL);
		options.addOption(noConsoleOption);
		CommandLineParser cmdLineParser = new DefaultParser();
		try{
			CommandLine cmdLine = cmdLineParser.parse(options, args);
			if (cmdLine.hasOption("help")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("deconvolution", options, true);
			}
			parseOptions (cmdLine);
			printArgumentValues(cmdLine);
		}
			catch(MissingOptionException e){
				HelpFormatter formatter = new HelpFormatter();
				DeconvolutionLogger.log.info(e.toString());
				formatter.printHelp("deconvolution", options, true);
				System.exit(0);
		}
	}
	
	private void parseOptions(CommandLine cmdLine) throws FileNotFoundException{
		if(cmdLine.hasOption("output_significant_only")){
			onlyOutputSignificant = !onlyOutputSignificant;
		}

		if(cmdLine.hasOption("remove_constraint_violating_samples")){
			removeConstraintViolatingSamples = !removeConstraintViolatingSamples;
		}
		permutationType = "genotype";
		if(cmdLine.hasOption("permutation_type")){
			permutationType = cmdLine.getOptionValue("permutation_type");
			if(!(permutationType.equals("genotype") || permutationType.equals("expression"))){
				throw new IllegalArgumentException("permutation_type should be genotype or expression, not "+cmdLine.getOptionValue("permutation_type"));
			}
		}
		if (cmdLine.hasOption("permute")) {
			numberOfPermutations = Integer.parseInt(cmdLine.getOptionValue("permute"));
			if(numberOfPermutations < 1){
				numberOfPermutations = 1;
			}
		}
		
		if (cmdLine.hasOption("round_dosage")) {
			roundDosage = true;
		}
		if (cmdLine.hasOption("force_normal_expression")){
			forceNormalExpression = !forceNormalExpression;
		}

		if (cmdLine.hasOption("force_normal_cellcount")){
			forceNormalCellcount = !forceNormalCellcount;
		}

		if (cmdLine.hasOption("all_dosages")){
			allDosages = !allDosages;
		}
		if (cmdLine.hasOption("skip_genotypes")){
			skipGenotypes = !skipGenotypes;
		}
		
		if (cmdLine.hasOption("minimum_samples_per_genotype")) {
			minimumSamplesPerGenotype = Integer.parseInt(cmdLine.getOptionValue("minimum_samples_per_genotype"));
			if(minimumSamplesPerGenotype < 0){
				minimumSamplesPerGenotype = 0;
			}
		}

		expressionFile = cmdLine.getOptionValue("expression");
		genotypeFile = cmdLine.getOptionValue("genotype");
		cellcountFile = cmdLine.getOptionValue("cellcount");
		snpsToTestFile = cmdLine.getOptionValue("snpsToTest");
		// check if all input files exist before starting the program to return error as early as possible
		if(!new File(expressionFile).exists() || new File(expressionFile).isDirectory()) { 
		    throw new FileNotFoundException(expressionFile+" does not exist");
		}
		if(!new File(genotypeFile).exists() || new File(genotypeFile).isDirectory()) { 
		    throw new FileNotFoundException(genotypeFile+" does not exist");
		}
		if(!new File(cellcountFile).exists() || new File(cellcountFile).isDirectory()) { 
		    throw new FileNotFoundException(cellcountFile+" does not exist");
		}
		if(!new File(snpsToTestFile).exists() || new File(snpsToTestFile).isDirectory()) { 
		    throw new FileNotFoundException(snpsToTestFile+" does not exist");
		}
				
		if (cmdLine.hasOption("outfile")) {
			outfile = cmdLine.getOptionValue("outfile");
		}
		
		outfolder = cmdLine.getOptionValue("outfolder");
		
		if (cmdLine.hasOption("no_console")) {
			noConsole = !noConsole;
		}
		if (cmdLine.hasOption("normalization_type")){
			normalizationType = cmdLine.getOptionValue("normalization_type");
		}
		if (cmdLine.hasOption("filter_samples")){
			filterSamples = !filterSamples;
		}
		if (cmdLine.hasOption("use_relative_cellcounts")){
			useRelativeCellCounts = !useRelativeCellCounts;
		}
		if (cmdLine.hasOption("validate_output")){
			validate = cmdLine.getOptionValue("validate_output");
			if(!new File(validate).exists() || new File(validate).isDirectory()) { 
			    throw new FileNotFoundException(validate+" does not exist");
			}	
		}
		if (cmdLine.hasOption("test_run")) {
			testRun = !testRun;
		}
		
		if (cmdLine.hasOption("multiple_testing_correction_method")){
			multipleTestCorrectionMethod = cmdLine.getOptionValue("multiple_testing_correction_method");
		}
		
		if (cmdLine.hasOption("whole_blood_qtl")){
			wholeBloodQTL = !wholeBloodQTL;
		}
	}
	

	private void printArgumentValues(CommandLine cmdLine){
	    try {
	    	File outfolderDir = new File(outfolder);
	    	// if the directory does not exist, create it
	    	Boolean dirDidNotExist = false;
	    	if (!outfolderDir.exists()) {
	    		outfolderDir.mkdir();
	    		dirDidNotExist = true;
	    	}
	    	DeconvolutionLogger.setup(outfolder, noConsole);
	    	if(dirDidNotExist){
	    		DeconvolutionLogger.log.info("Created directory "+outfolder);
	    	}
	    	DeconvolutionLogger.log.info("Writing output and logfile to "+outfolder);
	    } catch (IOException e) {
	      e.printStackTrace();
	      throw new RuntimeException("Problems with creating the log files");
	    }
	    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    Date date = new Date();
	    DeconvolutionLogger.log.info("Starting deconvolution");
	    DeconvolutionLogger.log.info(dateFormat.format(date));
	    DeconvolutionLogger.log.info("======= DECONVOLUTION paramater settings =======");
		DeconvolutionLogger.log.info(String.format("Expression file (-e): %s", expressionFile));
		DeconvolutionLogger.log.info(String.format("Genotype file (-g): %s", genotypeFile));
		DeconvolutionLogger.log.info(String.format("Cellcount file (-c): %s", cellcountFile));
		DeconvolutionLogger.log.info(String.format("SNPs to test file (-sn): %s", snpsToTestFile));
		DeconvolutionLogger.log.info(String.format("Outfolder (-o): %s", outfolder));
		DeconvolutionLogger.log.info(String.format("Outfile (-of): %s", outfile));
		DeconvolutionLogger.log.info(String.format("Number of permutations (-p): %s", numberOfPermutations));
		if(numberOfPermutations > 0){
			DeconvolutionLogger.log.info(String.format("Permutation type (-pt): %s", permutationType));
		}
		DeconvolutionLogger.log.info(String.format("Normalize expression (-ne): %s", forceNormalExpression));
		DeconvolutionLogger.log.info(String.format("Normalization type used if Normalize expression==true (-n): %s", normalizationType));
		DeconvolutionLogger.log.info(String.format("Normalize cellcounts (-ne): %s", forceNormalCellcount));
		DeconvolutionLogger.log.info(String.format("Round dosage (-r): %s", roundDosage));
		DeconvolutionLogger.log.info(String.format("Filter out QTLs where not all dosages are present in at least 1 sample (-ad): %s", allDosages));
		DeconvolutionLogger.log.info(String.format("Minimum samples per genotype (-m): %s", minimumSamplesPerGenotype));
		DeconvolutionLogger.log.info(String.format("Filter samples from output (-f): %s", filterSamples));
		DeconvolutionLogger.log.info(String.format("Remove constraint violating samples (-rc): %s", removeConstraintViolatingSamples));
		DeconvolutionLogger.log.info(String.format("Only output significant results (-s): %s", onlyOutputSignificant));
		DeconvolutionLogger.log.info(String.format("Use relative cellcounts (-cc): %s", useRelativeCellCounts));
		DeconvolutionLogger.log.info(String.format("test run doing only 100 QTL (-t): %s", testRun));
		DeconvolutionLogger.log.info(String.format("Multiple testing correction method (-mt): %s", multipleTestCorrectionMethod));
		DeconvolutionLogger.log.info(String.format("Skipping genotypes that are in SNP-gene pair file but not in genotype file (-sg): %s", skipGenotypes));
		DeconvolutionLogger.log.info(String.format("Add whole blood eQTL (pearson correlation genotypes and expression) (-w): %s",wholeBloodQTL));
		DeconvolutionLogger.log.info(String.format("Do not ouput logging info to console (-no): %s", noConsole));
		if(validate == null){
			DeconvolutionLogger.log.info(String.format("Validate (-v): %s", false));
		}
		else{
			DeconvolutionLogger.log.info(String.format("Validating using (-v): %s", validate));
		}
		DeconvolutionLogger.log.info("=================================================");
	}
	public String getExpressionFile(){
		return (expressionFile);
	}
	public String getSnpsToTestFile(){
		return (snpsToTestFile);
	}	
	public String getGenotypeFile(){
		return(genotypeFile);
	}
	public String getCellcountFile(){
		return(cellcountFile);
	}
	public String getOutfile(){
		return(outfile);
	}
	public int getNumberOfPermutations(){
		return(numberOfPermutations);
	}
	public String getPermutationType(){
		return(permutationType);
	}
	public Boolean getForceNormalExpression(){
		return(forceNormalExpression);
	}
	public Boolean getForceNormalCellcount(){
		return(forceNormalCellcount);
	}
	public int getMinimumSamplesPerGenotype(){
		return(minimumSamplesPerGenotype);
	}
	public Boolean getRoundDosage(){
		return(roundDosage);
	}

	public Boolean getAllDosages(){
		return(allDosages);
	}
	public String getOutfolder() throws IllegalAccessException{
		if(this.outfolder == null){
			throw new IllegalAccessException("Outfolder has not been set");
		}
		return(outfolder);
	}
	public String getNormalizationType(){
		return(normalizationType);
	}
	public void setOutfolder(String newOutfolder){
		outfolder = newOutfolder;
	}
	public Boolean getFilterSamples(){
		return(filterSamples);
	}
	public Boolean getRemoveConstraintViolatingSamples(){
		return(removeConstraintViolatingSamples);
	}

	public Boolean getOnlyOutputSignificant(){
		return(onlyOutputSignificant);
	}
	public Boolean getUseRelativeCellCounts(){
		return(useRelativeCellCounts);
	}
	public String getValidationFile(){
		return(validate);
	}
	public Boolean getTestRun(){
		return(testRun);
	}
	public String getMultipleTestingMethod(){
		return(multipleTestCorrectionMethod);
	}
	public Boolean getSkipGenotypes(){
		return(skipGenotypes);
	}
	public Boolean getWholeBloodQTL(){
		return(wholeBloodQTL);
	}
}





