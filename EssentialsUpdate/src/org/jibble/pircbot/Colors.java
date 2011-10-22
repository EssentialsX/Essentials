/* 
Copyright Paul James Mutton, 2001-2009, http://www.jibble.org/

This file is part of PircBot.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

*/


package org.jibble.pircbot;

/**
 * The Colors class provides several static fields and methods that you may
 * find useful when writing an IRC Bot.
 *  <p>
 * This class contains constants that are useful for formatting lines
 * sent to IRC servers.  These constants allow you to apply various
 * formatting to the lines, such as colours, boldness, underlining
 * and reverse text.
 *  <p>
 * The class contains static methods to remove colours and formatting
 * from lines of IRC text.
 *  <p>
 * Here are some examples of how to use the contants from within a
 * class that extends PircBot and imports org.jibble.pircbot.*;
 * 
 * <pre> sendMessage("#cs", Colors.BOLD + "A bold hello!");
 *     <b>A bold hello!</b>
 * sendMessage("#cs", Colors.RED + "Red" + Colors.NORMAL + " text");
 *     <font color="red">Red</font> text
 * sendMessage("#cs", Colors.BOLD + Colors.RED + "Bold and red");
 *     <b><font color="red">Bold and red</font></b></pre>
 * 
 * Please note that some IRC channels may be configured to reject any
 * messages that use colours.  Also note that older IRC clients may be
 * unable to correctly display lines that contain colours and other
 * control characters.
 *  <p>
 * Note that this class name has been spelt in the American style in
 * order to remain consistent with the rest of the Java API.
 *
 *
 * @since   0.9.12
 * @author  Paul James Mutton,
 *          <a href="http://www.jibble.org/">http://www.jibble.org/</a>
 * @version    1.5.0 (Build time: Mon Dec 14 20:07:17 2009)
 */
public class Colors {

    
    /**
     * Removes all previously applied color and formatting attributes.
     */
    public static final String NORMAL = "\u000f";


    /**
     * Bold text.
     */
    public static final String BOLD = "\u0002";
    

    /**
     * Underlined text.
     */
    public static final String UNDERLINE = "\u001f";


    /**
     * Reversed text (may be rendered as italic text in some clients).
     */
    public static final String REVERSE = "\u0016";
    

    /**
     * White coloured text.
     */
    public static final String WHITE = "\u000300";
    

    /**
     * Black coloured text.
     */
    public static final String BLACK = "\u000301";
    

    /**
     * Dark blue coloured text.
     */
    public static final String DARK_BLUE = "\u000302";
    

    /**
     * Dark green coloured text.
     */
    public static final String DARK_GREEN = "\u000303";
    

    /**
     * Red coloured text.
     */
    public static final String RED = "\u000304";
    

    /**
     * Brown coloured text.
     */
    public static final String BROWN = "\u000305";
    

    /**
     * Purple coloured text.
     */
    public static final String PURPLE = "\u000306";
    

    /**
     * Olive coloured text.
     */
    public static final String OLIVE = "\u000307";
    

    /**
     * Yellow coloured text.
     */
    public static final String YELLOW = "\u000308";
    

    /**
     * Green coloured text.
     */
    public static final String GREEN = "\u000309";
    

    /**
     * Teal coloured text.
     */
    public static final String TEAL = "\u000310";
    

    /**
     * Cyan coloured text.
     */
    public static final String CYAN = "\u000311";
    

    /**
     * Blue coloured text.
     */
    public static final String BLUE = "\u000312";
    

    /**
     * Magenta coloured text.
     */
    public static final String MAGENTA = "\u000313";
    

    /**
     * Dark gray coloured text.
     */
    public static final String DARK_GRAY = "\u000314";
    

    /**
     * Light gray coloured text.
     */
    public static final String LIGHT_GRAY = "\u000315";

    
    /**
     * This class should not be constructed.
     */
    private Colors() {
        
    }
    
    
    /**
     * Removes all colours from a line of IRC text.
     * 
     * @since PircBot 1.2.0
     * 
     * @param line the input text.
     * 
     * @return the same text, but with all colours removed.
     */
    public static String removeColors(String line) {
        int length = line.length();
        StringBuffer buffer = new StringBuffer();
        int i = 0;
        while (i < length) {
            char ch = line.charAt(i);
            if (ch == '\u0003') {
                i++;
                // Skip "x" or "xy" (foreground color).
                if (i < length) {
                    ch = line.charAt(i);
                    if (Character.isDigit(ch)) {
                        i++;
                        if (i < length) {
                            ch = line.charAt(i);
                            if (Character.isDigit(ch)) {
                                i++;
                            }
                        }
                        // Now skip ",x" or ",xy" (background color).
                        if (i < length) {
                            ch = line.charAt(i);
                            if (ch == ',') {
                                i++;
                                if (i < length) {
                                    ch = line.charAt(i);
                                    if (Character.isDigit(ch)) {
                                        i++;
                                        if (i < length) {
                                            ch = line.charAt(i);
                                            if (Character.isDigit(ch)) {
                                                i++;
                                            }
                                        }
                                    }
                                    else {
                                        // Keep the comma.
                                        i--;
                                    }
                                }
                                else {
                                    // Keep the comma.
                                    i--;
                                }
                            }
                        }
                    }
                }
            }
            else if (ch == '\u000f') {
                i++;
            }
            else {
                buffer.append(ch);
                i++;
            }
        }
        return buffer.toString();
    }
    
    
    /**
     * Remove formatting from a line of IRC text.
     * 
     * @since PircBot 1.2.0
     * 
     * @param line the input text.
     * 
     * @return the same text, but without any bold, underlining, reverse, etc.
     */
    public static String removeFormatting(String line) {
        int length = line.length();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            char ch = line.charAt(i);
            if (ch == '\u000f' || ch == '\u0002' || ch == '\u001f' || ch == '\u0016') {
                // Don't add this character.
            }
            else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }
    
    
    /**
     * Removes all formatting and colours from a line of IRC text.
     * 
     * @since PircBot 1.2.0
     *
     * @param line the input text.
     * 
     * @return the same text, but without formatting and colour characters.
     * 
     */
    public static String removeFormattingAndColors(String line) {
        return removeFormatting(removeColors(line));
    }
    
}
