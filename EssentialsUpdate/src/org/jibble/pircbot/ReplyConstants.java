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
 * This interface contains the values of all numeric replies specified
 * in section 6 of RFC 1459.  Refer to RFC 1459 for further information.
 *  <p>
 * If you override the onServerResponse method in the PircBot class,
 * you may find these constants useful when comparing the numeric
 * value of a given code.
 * 
 * @since   1.0.0
 * @author  Paul James Mutton,
 *          <a href="http://www.jibble.org/">http://www.jibble.org/</a>
 * @version    1.5.0 (Build time: Mon Dec 14 20:07:17 2009)
 */
public interface ReplyConstants {
    
    
    // Error Replies.
    public static final int ERR_NOSUCHNICK = 401;
    public static final int ERR_NOSUCHSERVER = 402;
    public static final int ERR_NOSUCHCHANNEL = 403;
    public static final int ERR_CANNOTSENDTOCHAN = 404;
    public static final int ERR_TOOMANYCHANNELS = 405;
    public static final int ERR_WASNOSUCHNICK = 406;
    public static final int ERR_TOOMANYTARGETS = 407;
    public static final int ERR_NOORIGIN = 409;
    public static final int ERR_NORECIPIENT = 411;
    public static final int ERR_NOTEXTTOSEND = 412;
    public static final int ERR_NOTOPLEVEL = 413;
    public static final int ERR_WILDTOPLEVEL = 414;
    public static final int ERR_UNKNOWNCOMMAND = 421;
    public static final int ERR_NOMOTD = 422;
    public static final int ERR_NOADMININFO = 423;
    public static final int ERR_FILEERROR = 424;
    public static final int ERR_NONICKNAMEGIVEN = 431;
    public static final int ERR_ERRONEUSNICKNAME = 432;
    public static final int ERR_NICKNAMEINUSE = 433;
    public static final int ERR_NICKCOLLISION = 436;
    public static final int ERR_USERNOTINCHANNEL = 441;
    public static final int ERR_NOTONCHANNEL = 442;
    public static final int ERR_USERONCHANNEL = 443;
    public static final int ERR_NOLOGIN = 444;
    public static final int ERR_SUMMONDISABLED = 445;
    public static final int ERR_USERSDISABLED = 446;
    public static final int ERR_NOTREGISTERED = 451;
    public static final int ERR_NEEDMOREPARAMS = 461;
    public static final int ERR_ALREADYREGISTRED = 462;
    public static final int ERR_NOPERMFORHOST = 463;
    public static final int ERR_PASSWDMISMATCH = 464;
    public static final int ERR_YOUREBANNEDCREEP = 465;
    public static final int ERR_KEYSET = 467;
    public static final int ERR_CHANNELISFULL = 471;
    public static final int ERR_UNKNOWNMODE = 472;
    public static final int ERR_INVITEONLYCHAN = 473;
    public static final int ERR_BANNEDFROMCHAN = 474;
    public static final int ERR_BADCHANNELKEY = 475;
    public static final int ERR_NOPRIVILEGES = 481;
    public static final int ERR_CHANOPRIVSNEEDED = 482;
    public static final int ERR_CANTKILLSERVER = 483;
    public static final int ERR_NOOPERHOST = 491;
    public static final int ERR_UMODEUNKNOWNFLAG = 501;
    public static final int ERR_USERSDONTMATCH = 502;
    
    
    // Command Responses.
    public static final int RPL_TRACELINK = 200;
    public static final int RPL_TRACECONNECTING = 201;
    public static final int RPL_TRACEHANDSHAKE = 202;
    public static final int RPL_TRACEUNKNOWN = 203;
    public static final int RPL_TRACEOPERATOR = 204;
    public static final int RPL_TRACEUSER = 205;
    public static final int RPL_TRACESERVER = 206;
    public static final int RPL_TRACENEWTYPE = 208;
    public static final int RPL_STATSLINKINFO = 211;
    public static final int RPL_STATSCOMMANDS = 212;
    public static final int RPL_STATSCLINE = 213;
    public static final int RPL_STATSNLINE = 214;
    public static final int RPL_STATSILINE = 215;
    public static final int RPL_STATSKLINE = 216;
    public static final int RPL_STATSYLINE = 218;
    public static final int RPL_ENDOFSTATS = 219;
    public static final int RPL_UMODEIS = 221;
    public static final int RPL_STATSLLINE = 241;
    public static final int RPL_STATSUPTIME = 242;
    public static final int RPL_STATSOLINE = 243;
    public static final int RPL_STATSHLINE = 244;
    public static final int RPL_LUSERCLIENT = 251;
    public static final int RPL_LUSEROP = 252;
    public static final int RPL_LUSERUNKNOWN = 253;
    public static final int RPL_LUSERCHANNELS = 254;
    public static final int RPL_LUSERME = 255;
    public static final int RPL_ADMINME = 256;
    public static final int RPL_ADMINLOC1 = 257;
    public static final int RPL_ADMINLOC2 = 258;
    public static final int RPL_ADMINEMAIL = 259;
    public static final int RPL_TRACELOG = 261;
    public static final int RPL_NONE = 300;
    public static final int RPL_AWAY = 301;
    public static final int RPL_USERHOST = 302;
    public static final int RPL_ISON = 303;
    public static final int RPL_UNAWAY = 305;
    public static final int RPL_NOWAWAY = 306;
    public static final int RPL_WHOISUSER = 311;
    public static final int RPL_WHOISSERVER = 312;
    public static final int RPL_WHOISOPERATOR = 313;
    public static final int RPL_WHOWASUSER = 314;
    public static final int RPL_ENDOFWHO = 315;
    public static final int RPL_WHOISIDLE = 317;
    public static final int RPL_ENDOFWHOIS = 318;
    public static final int RPL_WHOISCHANNELS = 319;
    public static final int RPL_LISTSTART = 321;
    public static final int RPL_LIST = 322;
    public static final int RPL_LISTEND = 323;
    public static final int RPL_CHANNELMODEIS = 324;
    public static final int RPL_NOTOPIC = 331;
    public static final int RPL_TOPIC = 332;
    public static final int RPL_TOPICINFO = 333;
    public static final int RPL_INVITING = 341;
    public static final int RPL_SUMMONING = 342;
    public static final int RPL_VERSION = 351;
    public static final int RPL_WHOREPLY = 352;
    public static final int RPL_NAMREPLY = 353;
    public static final int RPL_LINKS = 364;
    public static final int RPL_ENDOFLINKS = 365;
    public static final int RPL_ENDOFNAMES = 366;
    public static final int RPL_BANLIST = 367;
    public static final int RPL_ENDOFBANLIST = 368;
    public static final int RPL_ENDOFWHOWAS = 369;
    public static final int RPL_INFO = 371;
    public static final int RPL_MOTD = 372;
    public static final int RPL_ENDOFINFO = 374;
    public static final int RPL_MOTDSTART = 375;
    public static final int RPL_ENDOFMOTD = 376;
    public static final int RPL_YOUREOPER = 381;
    public static final int RPL_REHASHING = 382;
    public static final int RPL_TIME = 391;
    public static final int RPL_USERSSTART = 392;
    public static final int RPL_USERS = 393;
    public static final int RPL_ENDOFUSERS = 394;
    public static final int RPL_NOUSERS = 395;
    
    
    // Reserved Numerics.
    public static final int RPL_TRACECLASS = 209;
    public static final int RPL_STATSQLINE = 217;
    public static final int RPL_SERVICEINFO = 231;
    public static final int RPL_ENDOFSERVICES = 232;
    public static final int RPL_SERVICE = 233;
    public static final int RPL_SERVLIST = 234;
    public static final int RPL_SERVLISTEND = 235;
    public static final int RPL_WHOISCHANOP = 316;
    public static final int RPL_KILLDONE = 361;
    public static final int RPL_CLOSING = 362;
    public static final int RPL_CLOSEEND = 363;
    public static final int RPL_INFOSTART = 373;
    public static final int RPL_MYPORTIS = 384;
    public static final int ERR_YOUWILLBEBANNED = 466;
    public static final int ERR_BADCHANMASK = 476;
    public static final int ERR_NOSERVICEHOST = 492;
    
}
