package f00f.net.irc.martyr.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author Daniel Henninger
 */
public class IRCStringUtils
{

    /**
     * Returns the message with all control characters stripped from it.
     *
     * @param msg Message to remove control chars from.
     * @return Stripped form of message.
     */
    public static String stripControlChars(String msg)
    {
        Pattern pa = Pattern.compile("\u0003\\p{Digit}\\p{Digit}");
        Matcher ma = pa.matcher(msg);
        Pattern pb = Pattern.compile("\\p{Cntrl}");
        Matcher mb = pb.matcher(ma.replaceAll(""));
        return mb.replaceAll("");
    }

    /**
     * Returns the message with all formatting characters converted into associated html characters.
     *
     * TODO: Should actually parse colors.
     * @param msg Message to convert to HTML format.
     * @return Message in HTML format.
     */
    public static String convertToHTML(String msg)
    {
        CharacterIterator ci = new StringCharacterIterator(msg);
        String htmlStr = "";
        ArrayList<String> formatList = new ArrayList<String>();
        for (char c = ci.first(); c != CharacterIterator.DONE; c = ci.next()) {
            if (c == '\u0002') {
                if (formatList.contains("</b>")) {
                    formatList.remove("</b>");
                    htmlStr += "</b>";
                }
                else {
                    formatList.add("</b>");
                    htmlStr += "<b>";
                }
            }
            else if (c == '\u001F') {
                if (formatList.contains("</u>")) {
                    formatList.remove("</u>");
                    htmlStr += "</u>";
                }
                else {
                    formatList.add("</u>");
                    htmlStr += "<u>";
                }
            }
            else if (c == '\u0016') {
                if (formatList.contains("</i>")) {
                    formatList.remove("</i>");
                    htmlStr += "</i>";
                }
                else {
                    formatList.add("</i>");
                    htmlStr += "<i>";
                }
            }
            else if (c == '\u000F' || c == '\u0015') {
                for (String f : formatList) {
                    htmlStr += f;
                }
                formatList.clear();
            }
            else {
                htmlStr += c;
            }
        }
        for (String f : formatList) {
            htmlStr += f;
        }
        formatList.clear();
        return stripControlChars(htmlStr);
    }

}
