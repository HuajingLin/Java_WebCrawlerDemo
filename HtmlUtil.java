/*
 * For Extra Credit Assignment: Web Crawler and Word Count Ranking
 * Student: Huajing Lin
 * date: 12/7/2017
 */
package webcrawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {

    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
    private static final String regEx_space = "\t|\r|\n";//定义空格回车换行符
    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

    /**
     *    * @param htmlStr    * @return    * delete Html tag
   
     */
    private static String delHTMLTag(String htmlStr) {
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");
        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");
        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll("");
        return htmlStr.trim();
    }

    public static String getTextFromHtml(String htmlStr) {
        htmlStr = delHTMLTag(htmlStr);
        //htmlStr = htmlStr.replaceAll(" ", "");
        return htmlStr;
    }
}
