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
	int ulIndent=0,olIndent=0,qIndent=0;
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
				}else if(olIndent>0){
					pw.println("</ol>");
				}else if(qIndent>0){
					pw.println("</blockquote>");
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
		if(returnStr.matches("([^\\x00-\\x7F]|[a-zA-Z])+.*|[0-9][^.].*")||returnStr.isEmpty()){
			if (ulIndent==0&&preFlg&&olIndent==0) {
				
			}else{
				if (returnStr.endsWith("  ")) {
					returnStr = returnStr.replace("  ","</br>");
				}
				if (!returnStr.isEmpty()) {
					returnStr="<div>"+returnStr+"</div>";
				}
			}
			while (ulIndent>0) {
				ulIndent--;
				returnStr="</ul>\r\n"+returnStr;
			}
			while (olIndent>0) {
				olIndent--;
				returnStr="</ol>\r\n"+returnStr;
			}
			while (qIndent>0) {
				qIndent--;
				returnStr="</blockquote>\r\n"+returnStr;
			}
		}
		if (returnStr.startsWith("```")) {
			if (preFlg) {
				returnStr=returnStr.replaceFirst("```","</pre>");
				preFlg=false;
			}else{
				returnStr="<pre>";
				preFlg=true;
			}
		}
		if (returnStr.startsWith("#")) {
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
		}
		if(returnStr.matches("(-|\\*|\\+) +.+")){
			if(ulIndent<1) returnStr="<ul>\r\n"+returnStr;
			if(ulIndent>1) returnStr="</ul>\r\n"+returnStr;
			returnStr = returnStr.replaceFirst("-|\\*|\\+","<li>")+"</li>";
			ulIndent=1;
		}
		if(returnStr.matches("( {4}|\\t)(-|\\*|\\+) +.+")){
			if(ulIndent<2) returnStr="<ul>\r\n"+returnStr;
			if(ulIndent>2) returnStr="</ul>\r\n"+returnStr;
			returnStr = returnStr.replaceFirst("-|\\*|\\+","<li>")+"</li>";
			ulIndent=2;
		}
		if (returnStr.matches("[0-9]\\. +.+")) {
			if (olIndent<1) returnStr="<ol>\r\n"+returnStr;
			if(olIndent>1) returnStr="</ol>\r\n"+returnStr;
			returnStr=returnStr.replaceFirst("[0-9]\\. +","<li>")+"</li>";
			olIndent=1;
		}
		if (returnStr.matches("( {4}|\\t)[0-9]\\. +.+")) {
			if (olIndent<2) returnStr="<ol>\r\n"+returnStr;
			if(olIndent>2) returnStr="</ol>\r\n"+returnStr;
			returnStr=returnStr.replaceFirst("[0-9]\\. +","<li>")+"</li>";
			olIndent=2;
		}
		if(returnStr.matches("(>) +.+")){
			if(qIndent<1) returnStr="<blockquote>\r\n"+returnStr;
			if(qIndent>1) returnStr="</blockquote>\r\n"+returnStr;
			returnStr = returnStr.replaceFirst("(>) +(.+)","$2");
			qIndent=1;
		}
		if(returnStr.matches("(>>) +.+")){
			if(qIndent<2) returnStr="<blockquote>\r\n"+returnStr;
			if(qIndent>2) returnStr="</blockquote>\r\n"+returnStr;
			returnStr = returnStr.replaceFirst("(>>) +(.+)","$2");
			qIndent=2;
		}
		while (returnStr.matches("(\\s|.)*(\\*|_){3}.*(\\*|_){3}.*")) {
			returnStr=returnStr.replaceFirst("([^\\*_]+)(\\*|_){3}([^\\*_]+)(\\*|_){3}([^\\*_]+)","$1<strong><em>$3</em></strong>$5");
		}
		while (returnStr.matches("(\\s|.)*(\\*|_){2}.*(\\*|_){2}.*")) {
			returnStr=returnStr.replaceFirst("([^\\*_]+)(\\*|_){2}([^\\*_]+)(\\*|_){2}([^\\*_]+)","$1<strong>$3</strong>$5");
		}
		while (returnStr.matches("(\\s|.)*(\\*|_).*(\\*|_).*")) {
			returnStr=returnStr.replaceFirst("([^\\*_]+)(\\*|_)([^\\*_]+)(\\*|_)([^\\*_]+)","$1<em>$3</em>$5");
		}
		while(returnStr.matches("\\!(\\s|.)*\\[(.*)\\]\\((.*)\\)")){
			returnStr=returnStr.replaceFirst("\\!\\[(.*)\\]\\((.*)\\)","<a herf=\"$1\">$2</a>");
		}
		while(returnStr.matches("(\\s|.)*\\[(.*)\\]\\((.*)\\)")){
			returnStr=returnStr.replaceFirst("\\[(.*)\\]\\((.*)\\)","<img src=\"$2\" alt=\"$1\">");
		}
		return returnStr;
	}
}