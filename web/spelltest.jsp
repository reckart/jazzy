<%@ page language = "java" import ="com.swabunga.spell.jsp.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<jsp:useBean id="spellbean" class="SpellerBean" scope="request"/>
<html>
<head>
<title>Spell check demo</title>
	<link rel="stylesheet" href="/css/spell.css">
</head>
<body>
<h1>This is a spell check demo site</h1>
<table>
<tr>
<td valign=top><p>This is a demo of the spell check I am writing. I'm using Lawrence Phillips's DoubleMetaphone <a href="http://www.cuj.com/articles/2000/0006/0006d/0006d.htm?topic=articles">algorithm</a>, combined with a derivation of <a href="http://aspell.sourceforge.net/manual/8_How.html">aspell's</a> replace/insert/delete one character. This is a beta version, there is much room for tweaking the results.
<br><br>
The dictionary I took from <a href="http://www.rocketdownload.com/Details/Smal/quikdict.htm">QuikDict</a> but I'm not sure if it is in the public domain.
<br><br>
<h2>Facts:</h2>
	The 91,000 word dictionary takes 10 seconds to load and takes about 2 megabytes of memory. Once loaded, word lookups are done in O(1) constant time. Processing time is roughly 1/2 the load time. To reduce load time, it might be possible to serialize the processed dictionary to and from disk. With the upcoming JDK1.4, there is support for <a href="http://java.sun.com/j2se/1.4/docs/api/java/nio/MappedByteBuffer.html">MappedByteBuffers</a> which will allow the program to access the dictionary directly from disk without loading it into memory. In this case, lookup time will depend only on the speed of the disk.  
<br><br>
<h2>Feedback</h2>
If you are interested in this software please drop me a line at <a href="mailto:mai3116@rit.edu">mai3116@rit.edu</a>. What features would you like to see this in the API for this software? I'm a really smart Computer Science student at <a href="http://www.rit.edu">RIT</a> looking for employment! ;-) 
</td><td valign=top>
<html:form method="get" action="spellcheck">
Enter a word to spell check: <html:text property="word"/>
<br>Rank threshold <html:text property="threshold"/>
<html:submit value="suggest"/>
</html:form>
<hr>

<b>Spell word</b> <bean:write name="spellbean" property="word"/><br>
<b>Code word</b> <bean:write name="spellbean" property="code"/><br>

<logic:match name="spellbean" property="correct" value="true">
<b>Congratulations, <bean:write name="spellbean" property="word"/>  is correct </b><br>
</logic:match>

<logic:notMatch name="spellbean" property="correct" value="true">
<b>Suggestions</b> 
<br>
<logic:iterate id="word" name="spellbean" property="suggestions">
	<bean:write name="word" property="word"/>
	<b>score</b> <bean:write name="word" property="score"/><br>
</logic:iterate>
</logic:notMatch>
</td></table>
</body>
</html>
