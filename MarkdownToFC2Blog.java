import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

class MarkdownToFC2Blog {
	File inputFile,outputFile;
	boolean preFlg;
	int ulIndent=0;
	public static void main(String[] args) {
		if(args.length!=1) {String[] argsa={"test.md"};
	args=argsa;}
		MarkdownToFC2Blog markdownToFC2Blog = new MarkdownToFC2Blog();
		markdownToFC2Blog.myMain(args[0]);
	}
	
	void myMain(String fileName){
		try{
			inputFile = new File(fileName);
			outputFile = new File(fileName.replaceAll("\\..+","\\.html"));
			if(checkBeforeReadfile(inputFile)){
				BufferedReader inputBR = new BufferedReader(new  InputStreamReader(new FileInputStream(inputFile),"UTF-8"));
				PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8")));
				String str;
				while((str = inputBR.readLine())!=null){
					pw.println(parse(str));
				}
				if (ulIndent>0) {
					pw.println("</ul>");
				}
				pw.close();
				inputBR.close();
			}else{
				System.out.println("no file");
			}
		}catch (FileNotFoundException e) {
			System.out.println(e);
		}catch (IOException e){
			System.out.println(e);
		}
	}
	
	private boolean checkBeforeReadfile(File file){
		if(file.exists()){
			if(file.isFile()&&file.canRead()){
				return true;
			}
		}
		return false;
	}
	
	private String parse(String str){
		String returnStr=str;
		if(str.isEmpty()){
			
		}
		if(returnStr.matches("(([^\\x01-\\x7E]|[\\da-zA-Z])+.*)")){
			if (ulIndent==0&&preFlg) {
				
			}else{
				returnStr="<div>"+returnStr+"</div>";
			}
			while (ulIndent>0) {
				ulIndent--;
				returnStr="</ul>\n"+returnStr;
			}
		}
		if (returnStr.startsWith("```")) {
			if (preFlg) {
				returnStr=returnStr.replaceFirst("```","</pre>");
				preFlg=false;
			}else{
				returnStr=returnStr.replaceFirst("```","<pre>");
				preFlg=true;
			}
		}
		if (returnStr.startsWith("######")) {
			returnStr = returnStr.replace("######","<h6>")+"</h6>";
		}else if (returnStr.startsWith("#####")) {
			returnStr = returnStr.replace("#####","<h5>")+"</h5>";
		}else if (returnStr.startsWith("####")) {
			returnStr = returnStr.replace("####","<h4>")+"</h4>";
		}else if (returnStr.startsWith("###")) {
			returnStr = returnStr.replace("###","<h3>")+"</h3>";
		}else if (returnStr.startsWith("##")) {
			returnStr = returnStr.replace("##","<h2>")+"</h2>";
			System.out.println("FC2Blog is can't use <h2></h2>");
		}else if(returnStr.startsWith("#")){
			returnStr = returnStr.replace("#","<h1>")+"</h1>";
			System.out.println("FC2Blog is can't use <h1></h1>");
		}
		if(returnStr.startsWith("-")){
			if(ulIndent<1) returnStr="<ul>\n"+returnStr;
			if(ulIndent>1) returnStr="</ul>\n"+returnStr;
			returnStr = returnStr.replaceFirst("-","<li>")+"</li>";
			ulIndent=1;
		}
		if(returnStr.startsWith("    -")){
			if(ulIndent<2) returnStr="<ul>\n"+returnStr;
			if(ulIndent>2) returnStr="</ul>\n"+returnStr;
			returnStr = returnStr.replaceFirst("-","<li>")+"</li>";
			ulIndent=2;
		}
		if(returnStr.startsWith("\t-")){
			if(ulIndent<2) returnStr="<ul>\n"+returnStr;
			if(ulIndent>2) returnStr="</ul>\n"+returnStr;
			returnStr = returnStr.replaceFirst("-","<li>")+"</li>";
			ulIndent=2;
		}
		if(returnStr.startsWith("*")){
			if(ulIndent<1) returnStr="<ul>\n"+returnStr;
			if(ulIndent>1) returnStr="</ul>\n"+returnStr;
			returnStr = returnStr.replaceFirst("-","<li>")+"</li>";
			ulIndent=1;
		}
		if(returnStr.startsWith("    *")){
			if(ulIndent<2) returnStr="<ul>\n"+returnStr;
			if(ulIndent>2) returnStr="</ul>\n"+returnStr;
			returnStr = returnStr.replaceFirst("-","<li>")+"</li>";
			ulIndent=1;
		}
		if(returnStr.startsWith("\t*")){
			if(ulIndent<2) returnStr="<ul>\n"+returnStr;
			if(ulIndent>2) returnStr="</ul>\n"+returnStr;
			returnStr = returnStr.replaceFirst("-","<li>")+"</li>";
			ulIndent=2;
		}
		while(returnStr.contains("***")){
			returnStr=returnStr.replaceFirst("\\*\\*\\*","<em><strong>");
			returnStr=returnStr.replaceFirst("\\*\\*\\*","</em></strong>");
		}
		while(returnStr.contains("**")){
			returnStr=returnStr.replaceFirst("\\*\\*","<strong>");
			returnStr=returnStr.replaceFirst("\\*\\*","</strong>");
		}
		while(returnStr.contains("*")){
			returnStr=returnStr.replaceFirst("\\*","<em>");
			if (!returnStr.contains("*")) {
				returnStr=returnStr.replaceFirst("<em>","\\*");
				break;
			}
			returnStr=returnStr.replaceFirst("\\*","</em>");
		}
		if (returnStr.endsWith("  ")) {
			returnStr = str.replace("  ","</br>");
		}
		return returnStr;
	}
}