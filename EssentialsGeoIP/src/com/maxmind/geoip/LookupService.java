/**
 * LookupService.java
 *
 * Copyright (C) 2003 MaxMind LLC.  All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.maxmind.geoip;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * Provides a lookup service for information based on an IP address. The location of
 * a database file is supplied when creating a lookup service instance. The edition of
 * the database determines what information is available about an IP address. See the
 * DatabaseInfo class for further details.<p>
 *
 * The following code snippet demonstrates looking up the country that an IP
 * address is from:
 * <pre>
 * // First, create a LookupService instance with the location of the database.
 * LookupService lookupService = new LookupService("c:\\geoip.dat");
 * // Assume we have a String ipAddress (in dot-decimal form).
 * Country country = lookupService.getCountry(ipAddress);
 * System.out.println("The country is: " + country.getName());
 * System.out.println("The country code is: " + country.getCode());
 * </pre>
 *
 * In general, a single LookupService instance should be created and then reused
 * repeatedly.<p>
 *
 * <i>Tip:</i> Those deploying the GeoIP API as part of a web application may find it
 * difficult to pass in a File to create the lookup service, as the location of the
 * database may vary per deployment or may even be part of the web-application. In this
 * case, the database should be added to the classpath of the web-app. For example, by
 * putting it into the WEB-INF/classes directory of the web application. The following code
 * snippet demonstrates how to create a LookupService using a database that can be found
 * on the classpath:
 *
 * <pre>
 * String fileName = getClass().getResource("/GeoIP.dat").toExternalForm().substring(6);
 * LookupService lookupService = new LookupService(fileName);</pre>
 *
 * @author Matt Tucker (matt@jivesoftware.com)
 */
public class LookupService {

    /**
     * Database file.
     */
    private RandomAccessFile file = null;
    private File databaseFile = null;

    /**
     * Information about the database.
     */
    private DatabaseInfo databaseInfo = null;

    /**
     * The database type. Default is the country edition.
     */
    byte databaseType = DatabaseInfo.COUNTRY_EDITION;

    int databaseSegments[];
    int recordLength;
    
    String licenseKey;
    int dnsService = 0;
    int dboptions;
    byte dbbuffer[];
    byte index_cache[];
    long mtime;
    int last_netmask;
    private final static int US_OFFSET = 1;
    private final static int CANADA_OFFSET = 677;
    private final static int WORLD_OFFSET = 1353;
    private final static int FIPS_RANGE = 360;
    private final static int COUNTRY_BEGIN = 16776960;
    private final static int STATE_BEGIN_REV0 = 16700000;
    private final static int STATE_BEGIN_REV1 = 16000000;
    private final static int STRUCTURE_INFO_MAX_SIZE = 20;
    private final static int DATABASE_INFO_MAX_SIZE = 100;
    public final static int GEOIP_STANDARD = 0;
    public final static int GEOIP_MEMORY_CACHE = 1;
    public final static int GEOIP_CHECK_CACHE = 2;
    public final static int GEOIP_INDEX_CACHE = 4;
    public final static int GEOIP_UNKNOWN_SPEED = 0;
    public final static int GEOIP_DIALUP_SPEED = 1;
    public final static int GEOIP_CABLEDSL_SPEED = 2;
    public final static int GEOIP_CORPORATE_SPEED = 3;


    private final static int SEGMENT_RECORD_LENGTH = 3;
    private final static int STANDARD_RECORD_LENGTH = 3;
    private final static int ORG_RECORD_LENGTH = 4;
    private final static int MAX_RECORD_LENGTH = 4;

    private final static int MAX_ORG_RECORD_LENGTH = 300;
    private final static int FULL_RECORD_LENGTH = 60;

    private final Country UNKNOWN_COUNTRY = new Country("--", "N/A");

    private static final HashMap hashmapcountryCodetoindex = new HashMap(512);
    private static final HashMap hashmapcountryNametoindex = new HashMap(512);
    private static final String[] countryCode = {
	"--","AP","EU","AD","AE","AF","AG","AI","AL","AM","AN","AO","AQ","AR",
	"AS","AT","AU","AW","AZ","BA","BB","BD","BE","BF","BG","BH","BI","BJ",
	"BM","BN","BO","BR","BS","BT","BV","BW","BY","BZ","CA","CC","CD","CF",
	"CG","CH","CI","CK","CL","CM","CN","CO","CR","CU","CV","CX","CY","CZ",
	"DE","DJ","DK","DM","DO","DZ","EC","EE","EG","EH","ER","ES","ET","FI",
	"FJ","FK","FM","FO","FR","FX","GA","GB","GD","GE","GF","GH","GI","GL",
	"GM","GN","GP","GQ","GR","GS","GT","GU","GW","GY","HK","HM","HN","HR",
	"HT","HU","ID","IE","IL","IN","IO","IQ","IR","IS","IT","JM","JO","JP",
	"KE","KG","KH","KI","KM","KN","KP","KR","KW","KY","KZ","LA","LB","LC",
	"LI","LK","LR","LS","LT","LU","LV","LY","MA","MC","MD","MG","MH","MK",
	"ML","MM","MN","MO","MP","MQ","MR","MS","MT","MU","MV","MW","MX","MY",
	"MZ","NA","NC","NE","NF","NG","NI","NL","NO","NP","NR","NU","NZ","OM",
	"PA","PE","PF","PG","PH","PK","PL","PM","PN","PR","PS","PT","PW","PY",
	"QA","RE","RO","RU","RW","SA","SB","SC","SD","SE","SG","SH","SI","SJ",
	"SK","SL","SM","SN","SO","SR","ST","SV","SY","SZ","TC","TD","TF","TG",
	"TH","TJ","TK","TM","TN","TO","TL","TR","TT","TV","TW","TZ","UA","UG",
	"UM","US","UY","UZ","VA","VC","VE","VG","VI","VN","VU","WF","WS","YE",
	"YT","RS","ZA","ZM","ME","ZW","A1","A2","O1","AX","GG","IM","JE","BL",
	"MF"};

    private static final String[] countryName = {
	"N/A","Asia/Pacific Region","Europe","Andorra","United Arab Emirates",
	"Afghanistan","Antigua and Barbuda","Anguilla","Albania","Armenia",
	"Netherlands Antilles","Angola","Antarctica","Argentina","American Samoa",
	"Austria","Australia","Aruba","Azerbaijan","Bosnia and Herzegovina",
	"Barbados","Bangladesh","Belgium","Burkina Faso","Bulgaria","Bahrain",
	"Burundi","Benin","Bermuda","Brunei Darussalam","Bolivia","Brazil","Bahamas",
	"Bhutan","Bouvet Island","Botswana","Belarus","Belize","Canada",
	"Cocos (Keeling) Islands","Congo, The Democratic Republic of the",
	"Central African Republic","Congo","Switzerland","Cote D'Ivoire",
	"Cook Islands","Chile","Cameroon","China","Colombia","Costa Rica","Cuba",
	"Cape Verde","Christmas Island","Cyprus","Czech Republic","Germany",
	"Djibouti","Denmark","Dominica","Dominican Republic","Algeria","Ecuador",
	"Estonia","Egypt","Western Sahara","Eritrea","Spain","Ethiopia","Finland",
	"Fiji","Falkland Islands (Malvinas)","Micronesia, Federated States of",
	"Faroe Islands","France","France, Metropolitan","Gabon","United Kingdom",
	"Grenada","Georgia","French Guiana","Ghana","Gibraltar","Greenland","Gambia",
	"Guinea","Guadeloupe","Equatorial Guinea","Greece",
	"South Georgia and the South Sandwich Islands","Guatemala","Guam",
	"Guinea-Bissau","Guyana","Hong Kong","Heard Island and McDonald Islands",
	"Honduras","Croatia","Haiti","Hungary","Indonesia","Ireland","Israel","India",
	"British Indian Ocean Territory","Iraq","Iran, Islamic Republic of",
	"Iceland","Italy","Jamaica","Jordan","Japan","Kenya","Kyrgyzstan","Cambodia",
	"Kiribati","Comoros","Saint Kitts and Nevis",
	"Korea, Democratic People's Republic of","Korea, Republic of","Kuwait",
	"Cayman Islands","Kazakhstan","Lao People's Democratic Republic","Lebanon",
	"Saint Lucia","Liechtenstein","Sri Lanka","Liberia","Lesotho","Lithuania",
	"Luxembourg","Latvia","Libyan Arab Jamahiriya","Morocco","Monaco",
	"Moldova, Republic of","Madagascar","Marshall Islands",
	"Macedonia","Mali","Myanmar","Mongolia",
	"Macau","Northern Mariana Islands","Martinique","Mauritania","Montserrat",
	"Malta","Mauritius","Maldives","Malawi","Mexico","Malaysia","Mozambique",
	"Namibia","New Caledonia","Niger","Norfolk Island","Nigeria","Nicaragua",
	"Netherlands","Norway","Nepal","Nauru","Niue","New Zealand","Oman","Panama",
	"Peru","French Polynesia","Papua New Guinea","Philippines","Pakistan",
	"Poland","Saint Pierre and Miquelon","Pitcairn Islands","Puerto Rico","" +
	"Palestinian Territory","Portugal","Palau","Paraguay","Qatar",
	"Reunion","Romania","Russian Federation","Rwanda","Saudi Arabia",
	"Solomon Islands","Seychelles","Sudan","Sweden","Singapore","Saint Helena",
	"Slovenia","Svalbard and Jan Mayen","Slovakia","Sierra Leone","San Marino",
	"Senegal","Somalia","Suriname","Sao Tome and Principe","El Salvador",
	"Syrian Arab Republic","Swaziland","Turks and Caicos Islands","Chad",
	"French Southern Territories","Togo","Thailand","Tajikistan","Tokelau",
	"Turkmenistan","Tunisia","Tonga","Timor-Leste","Turkey","Trinidad and Tobago",
	"Tuvalu","Taiwan","Tanzania, United Republic of","Ukraine","Uganda",
	"United States Minor Outlying Islands","United States","Uruguay","Uzbekistan",
	"Holy See (Vatican City State)","Saint Vincent and the Grenadines",
	"Venezuela","Virgin Islands, British","Virgin Islands, U.S.","Vietnam",
	"Vanuatu","Wallis and Futuna","Samoa","Yemen","Mayotte","Serbia",
	"South Africa","Zambia","Montenegro","Zimbabwe","Anonymous Proxy",
	"Satellite Provider","Other","Aland Islands","Guernsey","Isle of Man","Jersey",
	"Saint Barthelemy","Saint Martin"};


    /* init the hashmap once at startup time */
    static {
        int i;
        if(countryCode.length!=countryName.length)
            throw new AssertionError("countryCode.length!=countryName.length");
              
        // distributed service only
        for (i = 0; i < countryCode.length ;i++){
            hashmapcountryCodetoindex.put(countryCode[i],Integer.valueOf(i));
            hashmapcountryNametoindex.put(countryName[i],Integer.valueOf(i));
        }
    };


    /**
     * Create a new distributed lookup service using the license key
     *
     * @param databaseFile String representation of the database file.
     * @param licenseKey license key provided by Maxmind to access distributed service
     */
    public LookupService(String databaseFile,String licenseKey) throws IOException {
        this(new File(databaseFile));
        this.licenseKey = licenseKey;
        dnsService = 1;
    }
    /**
     * Create a new distributed lookup service using the license key
     *
     * @param databaseFile the database file.
     * @param licenseKey license key provided by Maxmind to access distributed service
     */
    public LookupService(File databaseFile,String licenseKey) throws IOException {
        this(databaseFile);
        this.licenseKey = licenseKey;
        dnsService = 1;
    }
    /**
     * Create a new distributed lookup service using the license key
     *
     * @param options  Resevered for future use
     * @param licenseKey license key provided by Maxmind to access distributed service
     */
    public LookupService(int options,String licenseKey) throws IOException {
        this.licenseKey = licenseKey;
        dnsService = 1;
        init();
    }
    /**
     * Create a new lookup service using the specified database file.
     *
     * @param databaseFile String representation of the database file.
     * @throws java.io.IOException if an error occured creating the lookup service
     *      from the database file.
     */
    public LookupService(String databaseFile) throws IOException {
        this(new File(databaseFile));
    }

    /**
     * Create a new lookup service using the specified database file.
     *
     * @param databaseFile the database file.
     * @throws java.io.IOException if an error occured creating the lookup service
     *      from the database file.
     */
    public LookupService(File databaseFile) throws IOException {
        this.databaseFile = databaseFile;
        this.file = new RandomAccessFile(databaseFile, "r");
        init();
    }

    /**
     * Create a new lookup service using the specified database file.
     *
     * @param databaseFile String representation of the database file.
     * @param options database flags to use when opening the database
     * GEOIP_STANDARD read database from disk
     * GEOIP_MEMORY_CACHE cache the database in RAM and read it from RAM
     * @throws java.io.IOException if an error occured creating the lookup service
     *      from the database file.
     */
    public LookupService(String databaseFile, int options) throws IOException{
        this(new File(databaseFile),options);
    }

    /**
     * Create a new lookup service using the specified database file.
     *
     * @param databaseFile the database file.
     * @param options database flags to use when opening the database
     * GEOIP_STANDARD read database from disk
     * GEOIP_MEMORY_CACHE cache the database in RAM and read it from RAM
     * @throws java.io.IOException if an error occured creating the lookup service
     *      from the database file.
     */
    public LookupService(File databaseFile, int options) throws IOException{
        this.databaseFile = databaseFile;
	this.file = new RandomAccessFile(databaseFile, "r");
	dboptions = options;
	init();
    }
    /**
     * Reads meta-data from the database file.
     *
     * @throws java.io.IOException if an error occurs reading from the database file.
     */
    private void init() throws IOException {
        int i, j;
        byte [] delim = new byte[3];
        byte [] buf = new byte[SEGMENT_RECORD_LENGTH];

	if (file == null) {
          return;
	}
	if ((dboptions & GEOIP_CHECK_CACHE) != 0) {
            mtime = databaseFile.lastModified();
	}
	file.seek(file.length() - 3);
        for (i = 0; i < STRUCTURE_INFO_MAX_SIZE; i++) {
            file.readFully(delim);
            if (delim[0] == -1 && delim[1] == -1 && delim[2] == -1) {
                databaseType = file.readByte();
                if (databaseType >= 106) {
                    // Backward compatibility with databases from April 2003 and earlier
                    databaseType -= 105;
                }
                // Determine the database type.
                if (databaseType == DatabaseInfo.REGION_EDITION_REV0) {
                    databaseSegments = new int[1];
                    databaseSegments[0] = STATE_BEGIN_REV0;
                    recordLength = STANDARD_RECORD_LENGTH;
                }else if (databaseType == DatabaseInfo.REGION_EDITION_REV1){
                    databaseSegments = new int[1];
                    databaseSegments[0] = STATE_BEGIN_REV1;
                    recordLength = STANDARD_RECORD_LENGTH;
		}
                else if (databaseType == DatabaseInfo.CITY_EDITION_REV0 ||
			 databaseType == DatabaseInfo.CITY_EDITION_REV1 ||
			 databaseType == DatabaseInfo.ORG_EDITION ||
			 databaseType == DatabaseInfo.ISP_EDITION ||
			 databaseType == DatabaseInfo.ASNUM_EDITION) {
			databaseSegments = new int[1];
			databaseSegments[0] = 0;
			if (databaseType == DatabaseInfo.CITY_EDITION_REV0 ||
			    databaseType == DatabaseInfo.CITY_EDITION_REV1 ||
			    databaseType == DatabaseInfo.ASNUM_EDITION) {
			    recordLength = STANDARD_RECORD_LENGTH;
			}
			else {
			    recordLength = ORG_RECORD_LENGTH;
			}
			file.readFully(buf);
			for (j = 0; j < SEGMENT_RECORD_LENGTH; j++) {
			    databaseSegments[0] += (unsignedByteToInt(buf[j]) << (j * 8));
			}
		    }
                break;
            }
            else {
                file.seek(file.getFilePointer() - 4);
            }
        }
        if ((databaseType == DatabaseInfo.COUNTRY_EDITION) ||
            (databaseType == DatabaseInfo.COUNTRY_EDITION_V6) ||
	    (databaseType == DatabaseInfo.PROXY_EDITION) ||
	    (databaseType == DatabaseInfo.NETSPEED_EDITION)) {
            databaseSegments = new int[1];
            databaseSegments[0] = COUNTRY_BEGIN;
            recordLength = STANDARD_RECORD_LENGTH;
        }
        if ((dboptions & GEOIP_MEMORY_CACHE) == 1) {
	    int l = (int) file.length();
	    dbbuffer = new byte[l];
	    file.seek(0);
	    file.readFully(dbbuffer,0,l);
	    databaseInfo = this.getDatabaseInfo();
	    file.close();
	}
        if ((dboptions & GEOIP_INDEX_CACHE) != 0) {
          int l = databaseSegments[0] * recordLength * 2;
          index_cache = new byte[l];
          if (index_cache != null){
            file.seek(0);
            file.readFully(index_cache,0,l);     
          }          
        } else {
          index_cache = null;
        }
     }

    /**
     * Closes the lookup service.
     */
    public void close() {
	try {
	    if (file != null){
		file.close();
	    }
            file = null;
        }
        catch (Exception e) { }
    }

    /**
     * Returns the country the IP address is in.
     *
     * @param ipAddress String version of an IPv6 address, i.e. "::127.0.0.1"
     * @return the country the IP address is from.
     */
    public Country getCountryV6(String ipAddress) {
	InetAddress addr;
	try {
	    addr = Inet6Address.getByName(ipAddress);
	}
	catch (UnknownHostException e) {
            return UNKNOWN_COUNTRY;
	}
	return getCountryV6(addr);
    }

    /**
     * Returns the country the IP address is in.
     *
     * @param ipAddress String version of an IP address, i.e. "127.0.0.1"
     * @return the country the IP address is from.
     */
    public Country getCountry(String ipAddress) {
	InetAddress addr;
	try {
	    addr = InetAddress.getByName(ipAddress);
	}
	catch (UnknownHostException e) {
            return UNKNOWN_COUNTRY;
	}
	return getCountry(bytesToLong(addr.getAddress()));
    }

    /**
     * Returns the country the IP address is in.
     *
     * @param ipAddress the IP address.
     * @return the country the IP address is from.
     */
    public synchronized Country getCountry(InetAddress ipAddress) {
        return getCountry(bytesToLong(ipAddress.getAddress()));
    }

    /**
     * Returns the country the IP address is in.
     *
     * @param addr the IP address as Inet6Address.
     * @return the country the IP address is from.
     */
    public Country getCountryV6(InetAddress addr) {
        if (file == null && (dboptions & GEOIP_MEMORY_CACHE) == 0) {
            throw new IllegalStateException("Database has been closed.");
        }
        int ret = seekCountryV6(addr) - COUNTRY_BEGIN;
        if (ret == 0) {
            return UNKNOWN_COUNTRY;
        }
        else {
            return new Country(countryCode[ret], countryName[ret]);
        }
    }

     /**
     * Returns the country the IP address is in.
     *
     * @param ipAddress the IP address in long format.
     * @return the country the IP address is from.
     */
    public Country getCountry(long ipAddress) {
        if (file == null && (dboptions & GEOIP_MEMORY_CACHE) == 0) {
            throw new IllegalStateException("Database has been closed.");
        }
        int ret = seekCountry(ipAddress) - COUNTRY_BEGIN;
        if (ret == 0) {
            return UNKNOWN_COUNTRY;
        }
        else {
            return new Country(countryCode[ret], countryName[ret]);
        }
    }

    public int getID(String ipAddress) {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(ipAddress);
        }
        catch (UnknownHostException e) {
            return 0;
        }
        return getID(bytesToLong(addr.getAddress()));
    }

    public int getID(InetAddress ipAddress) {
        return getID(bytesToLong(ipAddress.getAddress()));
    }

    public synchronized int getID(long ipAddress) {
        if (file == null && (dboptions & GEOIP_MEMORY_CACHE) == 0) {
            throw new IllegalStateException("Database has been closed.");
        }
	int ret = seekCountry(ipAddress) - databaseSegments[0];
	return ret;
    }

    public int last_netmask() {
      return this.last_netmask; 
    }

    public void netmask(int nm){
      this.last_netmask = nm;
    }

    /**
     * Returns information about the database.
     *
     * @return database info.
     */
    public synchronized DatabaseInfo getDatabaseInfo() {
        if (databaseInfo != null) {
            return databaseInfo;
        }
        try {
            _check_mtime();
            boolean hasStructureInfo = false;
            byte [] delim = new byte[3];
            // Advance to part of file where database info is stored.
            file.seek(file.length() - 3);
            for (int i=0; i<STRUCTURE_INFO_MAX_SIZE; i++) {
                int read = file.read( delim );
                if( read==3 && (delim[0]&0xFF)==255 && (delim[1]&0xFF) == 255 && (delim[2]&0xFF)==255 ){
                        hasStructureInfo = true;
                        break;
                }
                file.seek(file.getFilePointer() - 4);

            }
            if (hasStructureInfo) {
                    file.seek(file.getFilePointer() - 6);
            }
            else {
                // No structure info, must be pre Sep 2002 database, go back to end.
                file.seek(file.length() - 3);
            }
            // Find the database info string.
            for (int i=0; i<DATABASE_INFO_MAX_SIZE; i++) {
                file.readFully(delim);
                    if (delim[0]==0 && delim[1]==0 && delim[2]==0) {
                        byte[] dbInfo = new byte[i];
                    file.readFully(dbInfo);
                        // Create the database info object using the string.
                        this.databaseInfo = new DatabaseInfo(new String(dbInfo));
                        return databaseInfo;
                    }
                    file.seek(file.getFilePointer() -4);
                }
            }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new DatabaseInfo("");
    }

    synchronized void _check_mtime(){
      try {
        if ((dboptions & GEOIP_CHECK_CACHE) != 0){
          long t = databaseFile.lastModified();
          if (t != mtime){
            /* GeoIP Database file updated */
            /* refresh filehandle */
            file.close();
            file = new RandomAccessFile(databaseFile,"r");
	    databaseInfo = null;
	    init();
          }
        }
      } catch (IOException e) {
        System.out.println("file not found");
      }
    }

    // for GeoIP City only
    public Location getLocation(InetAddress addr) {
        return getLocation(bytesToLong(addr.getAddress()));
    }

    // for GeoIP City only
    public Location getLocation(String str) {
        if (dnsService == 0) {
            InetAddress addr;
            try {
                addr = InetAddress.getByName(str);
            }
            catch (UnknownHostException e) {
                return null;
            }

            return getLocation(addr);
        } else {
            String str2 = getDnsAttributes(str);
 	    return getLocationwithdnsservice(str2);
	    // TODO if DNS is not available, go to local file as backup
	}
    }

    String getDnsAttributes(String ip) {
        try {
            Hashtable env = new Hashtable();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
	    // TODO don't specify ws1, instead use ns servers for s.maxmind.com
            env.put("java.naming.provider.url","dns://ws1.maxmind.com/");

            DirContext ictx = new InitialDirContext(env);
            Attributes attrs = ictx.getAttributes(licenseKey + "." + ip + ".s.maxmind.com", new String[] {"txt"});
            //System.out.println(attrs.get("txt").get());
            String str = attrs.get("txt").get().toString();
            return str;
        }
        catch(NamingException e) {
	    // TODO fix this to handle exceptions
            System.out.println("DNS error");
            return null;
        }

    }

    public Location getLocationwithdnsservice(String str) {
        Location record = new Location();
        String key;
        String value;
        StringTokenizer st = new StringTokenizer(str,";=\""); 
        while (st.hasMoreTokens()) {
	    key = st.nextToken();
            if (st.hasMoreTokens()) {
                value = st.nextToken();
            } else {
	        value = "";}
	    if (key.equals("co")) {
		Integer i = (Integer)hashmapcountryCodetoindex.get(value);
		record.countryCode = value;
		record.countryName = countryName[i.intValue()];
	    }
	    if (key.equals("ci")) {
		record.city = value;
	    }
	    if (key.equals("re")) {
		record.region = value;
	    }
	    if (key.equals("zi")) {
	        record.postalCode = value;
	    }
	    // TODO, ISP and Organization
	    //if (key.equals("or")) {
	    //record.org = value;
	    //}
	    //if (key.equals("is")) {
	    //record.isp = value;
	    //}
	    if (key.equals("la")) {
		try{
		    record.latitude = Float.parseFloat(value);
		} catch(NumberFormatException e) {
		    record.latitude = 0;
		}
	    }
	    if (key.equals("lo")) {
		try{
		    record.longitude = Float.parseFloat(value);
		} catch(NumberFormatException e) {
		    record.latitude = 0;
		}
	    }
	    // dm depreciated use me ( metro_code ) instead
	    if (key.equals("dm") || key.equals("me")) {
		try{
		    record.metro_code = record.dma_code = Integer.parseInt(value);
		} catch(NumberFormatException e) {
		    record.metro_code = record.dma_code = 0;
		}
	    }
	    if (key.equals("ac")) {
		try{
		    record.area_code = Integer.parseInt(value);
		} catch(NumberFormatException e) {
		    record.area_code = 0;
		}
	    }
	}
        return record;
    }

    public synchronized Region getRegion(String str) {
	InetAddress addr;
	try {
	    addr = InetAddress.getByName(str);
	}
	catch (UnknownHostException e) {
	    return null;
	}

	return getRegion(bytesToLong(addr.getAddress()));
    }

    public synchronized Region getRegion(long ipnum) {
        Region record = new Region();
        int seek_region = 0;
        if (databaseType == DatabaseInfo.REGION_EDITION_REV0) {
            seek_region = seekCountry(ipnum) - STATE_BEGIN_REV0;
            char ch[] = new char[2];
            if (seek_region >= 1000) {
                record.countryCode = "US";
                record.countryName = "United States";
                ch[0] = (char)(((seek_region - 1000)/26) + 65);
                ch[1] = (char)(((seek_region - 1000)%26) + 65);
	        record.region = new String(ch);
            } else {
                record.countryCode = countryCode[seek_region];
                record.countryName = countryName[seek_region];
                record.region = "";
            }
        } else if (databaseType == DatabaseInfo.REGION_EDITION_REV1) {
            seek_region = seekCountry(ipnum) - STATE_BEGIN_REV1;
            char ch[] = new char[2];
            if (seek_region < US_OFFSET) {
                record.countryCode = "";
                record.countryName = "";
	        record.region = "";
            } else if (seek_region < CANADA_OFFSET) {
                record.countryCode = "US";
                record.countryName = "United States";
                ch[0] = (char)(((seek_region - US_OFFSET)/26) + 65);
                ch[1] = (char)(((seek_region - US_OFFSET)%26) + 65);
	        record.region = new String(ch);
            } else if (seek_region < WORLD_OFFSET) {
                record.countryCode = "CA";
                record.countryName = "Canada";
                ch[0] = (char)(((seek_region - CANADA_OFFSET)/26) + 65);
                ch[1] = (char)(((seek_region - CANADA_OFFSET)%26) + 65);
	        record.region = new String(ch);
            } else {
                record.countryCode = countryCode[(seek_region - WORLD_OFFSET) / FIPS_RANGE];
                record.countryName = countryName[(seek_region - WORLD_OFFSET) / FIPS_RANGE];
                record.region = "";
            }
	}
	return record;
    }

    public synchronized Location getLocation(long ipnum) {
        int record_pointer;
        byte record_buf[] = new byte[FULL_RECORD_LENGTH];
        int record_buf_offset = 0;
        Location record = new Location();
        int str_length = 0;
        int j, seek_country;
        double latitude = 0, longitude = 0;

        try {
            seek_country = seekCountry(ipnum);
            if (seek_country == databaseSegments[0]) {
                return null;
            }
            record_pointer = seek_country + (2 * recordLength - 1) * databaseSegments[0];

            if ((dboptions & GEOIP_MEMORY_CACHE) == 1) {
                //read from memory
		System.arraycopy(dbbuffer, record_pointer, record_buf, 0, Math.min(dbbuffer.length - record_pointer, FULL_RECORD_LENGTH));
} else {
                //read from disk
                file.seek(record_pointer);
                file.readFully(record_buf);
            }

            // get country
            record.countryCode = countryCode[unsignedByteToInt(record_buf[0])];
            record.countryName = countryName[unsignedByteToInt(record_buf[0])];
            record_buf_offset++;

            // get region
            while (record_buf[record_buf_offset + str_length] != '\0')
                str_length++;
            if (str_length > 0) {
                record.region = new String(record_buf, record_buf_offset, str_length);
            }
            record_buf_offset += str_length + 1;
            str_length = 0;

            // get city
            while (record_buf[record_buf_offset + str_length] != '\0')
                str_length++;
            if (str_length > 0) {
                record.city = new String(record_buf, record_buf_offset, str_length, "ISO-8859-1");
            }
            record_buf_offset += str_length + 1;
            str_length = 0;

            // get postal code
            while (record_buf[record_buf_offset + str_length] != '\0')
                str_length++;
            if (str_length > 0) {
                record.postalCode = new String(record_buf, record_buf_offset, str_length);
            }
            record_buf_offset += str_length + 1;

            // get latitude
            for (j = 0; j < 3; j++)
                latitude += (unsignedByteToInt(record_buf[record_buf_offset + j]) << (j * 8));
            record.latitude = (float) latitude/10000 - 180;
            record_buf_offset += 3;

            // get longitude
            for (j = 0; j < 3; j++)
                longitude += (unsignedByteToInt(record_buf[record_buf_offset + j]) << (j * 8));
	    record.longitude = (float) longitude/10000 - 180;

	    record.dma_code = record.metro_code = 0;
	    record.area_code = 0;
	    if (databaseType == DatabaseInfo.CITY_EDITION_REV1) {
		// get DMA code
		int metroarea_combo = 0;
		if (record.countryCode == "US") {
		    record_buf_offset += 3;
		    for (j = 0; j < 3; j++)
			metroarea_combo += (unsignedByteToInt(record_buf[record_buf_offset + j]) << (j * 8));
		    record.metro_code = record.dma_code = metroarea_combo/1000;
		    record.area_code = metroarea_combo % 1000;
		}
            }
	}
	catch (IOException e) {
            System.err.println("IO Exception while seting up segments");
        }
        return record;
    }

    public String getOrg(InetAddress addr) {
        return getOrg(bytesToLong(addr.getAddress()));
    }

    public String getOrg(String str) {
        InetAddress addr;
	try {
	    addr = InetAddress.getByName(str);
	}
	catch (UnknownHostException e) {
            return null;
	}
	return getOrg(addr);
    }

    // GeoIP Organization and ISP Edition methods
    public synchronized String getOrg(long ipnum) {
        int seek_org;
        int record_pointer;
        int str_length = 0;
        byte [] buf = new byte[MAX_ORG_RECORD_LENGTH];
        String org_buf;

        try {
            seek_org = seekCountry(ipnum);
            if (seek_org == databaseSegments[0]) {
		return null;
            }

            record_pointer = seek_org + (2 * recordLength - 1) * databaseSegments[0];
            if ((dboptions & GEOIP_MEMORY_CACHE) == 1) {
                //read from memory
		System.arraycopy(dbbuffer, record_pointer, buf, 0, Math.min(dbbuffer.length - record_pointer, MAX_ORG_RECORD_LENGTH));
            } else {
		//read from disk
                file.seek(record_pointer);
                file.readFully(buf);
            }
            while (buf[str_length] != '\0') {
		str_length++;
            }
            org_buf = new String(buf, 0, str_length, "ISO-8859-1");
            return org_buf;
        }
        catch (IOException e) {
            System.out.println("IO Exception");
            return null;
        }
    }

    /**
     * Finds the country index value given an IPv6 address.
     *
     * @param addr the ip address to find in long format.
     * @return the country index.
     */
    private synchronized int seekCountryV6(InetAddress addr) {
        byte [] v6vec = addr.getAddress();
	byte [] buf = new byte[2 * MAX_RECORD_LENGTH];
	int [] x = new int[2];
        int offset = 0;
        _check_mtime();
        for (int depth = 127; depth >= 0; depth--) {
            if ((dboptions & GEOIP_MEMORY_CACHE) == 1) {
		//read from memory
                for (int i = 0;i < 2 * MAX_RECORD_LENGTH;i++) {
		    buf[i] = dbbuffer[(2 * recordLength * offset)+i];
		}
            } else if ((dboptions & GEOIP_INDEX_CACHE) != 0) {
                //read from index cache
                for (int i = 0;i < 2 * MAX_RECORD_LENGTH;i++) {
		    buf[i] = index_cache[(2 * recordLength * offset)+i];
		}            
            } else {
		//read from disk 
		try {
                    file.seek(2 * recordLength * offset);
                    file.readFully(buf);
                }
                catch (IOException e) {
                    System.out.println("IO Exception");
                }
            }
            for (int i = 0; i<2; i++) {
                x[i] = 0;
                for (int j = 0; j<recordLength; j++) {
                    int y = buf[i*recordLength+j];
                    if (y < 0) {
                        y+= 256;
                    }
                    x[i] += (y << (j * 8));
                }
            }

            int bnum = 127 - depth;
            int idx = bnum >> 3;
            int b_mask = 1 << ( bnum & 7 ^ 7 );
            if ((v6vec[idx] & b_mask) > 0) {
                if (x[1] >= databaseSegments[0]) {
                    last_netmask = 128 - depth;
                    return x[1];
                }
                offset = x[1];
            }
            else {
                if (x[0] >= databaseSegments[0]) {
                    last_netmask = 128 - depth;
                    return x[0];
                }
                offset = x[0];
	    }
	}

        // shouldn't reach here
        System.err.println("Error seeking country while seeking " + addr.getHostAddress() );
        return 0;
    }
    /**
     * Finds the country index value given an IP address.
     *
     * @param ipAddress the ip address to find in long format.
     * @return the country index.
     */
    private synchronized int seekCountry(long ipAddress) {
	byte [] buf = new byte[2 * MAX_RECORD_LENGTH];
	int [] x = new int[2];
        int offset = 0;
        _check_mtime();
        for (int depth = 31; depth >= 0; depth--) {
            if ((dboptions & GEOIP_MEMORY_CACHE) == 1) {
		//read from memory
                for (int i = 0;i < 2 * MAX_RECORD_LENGTH;i++) {
		    buf[i] = dbbuffer[(2 * recordLength * offset)+i];
		}
            } else if ((dboptions & GEOIP_INDEX_CACHE) != 0) {
                //read from index cache
                for (int i = 0;i < 2 * MAX_RECORD_LENGTH;i++) {
		    buf[i] = index_cache[(2 * recordLength * offset)+i];
		}            
            } else {
		//read from disk 
		try {
                    file.seek(2 * recordLength * offset);
                    file.readFully(buf);
                }
                catch (IOException e) {
                    System.out.println("IO Exception");
                }
            }
            for (int i = 0; i<2; i++) {
                x[i] = 0;
                for (int j = 0; j<recordLength; j++) {
                    int y = buf[i*recordLength+j];
                    if (y < 0) {
                        y+= 256;
                    }
                    x[i] += (y << (j * 8));
                }
            }

            if ((ipAddress & (1 << depth)) > 0) {
                if (x[1] >= databaseSegments[0]) {
                    last_netmask = 32 - depth;
                    return x[1];
                }
                offset = x[1];
            }
            else {
                if (x[0] >= databaseSegments[0]) {
                    last_netmask = 32 - depth;
                    return x[0];
                }
                offset = x[0];
	    }
	}

        // shouldn't reach here
        System.err.println("Error seeking country while seeking " + ipAddress);
        return 0;
    }

    /**
     * Returns the long version of an IP address given an InetAddress object.
     *
     * @param address the InetAddress.
     * @return the long form of the IP address.
     */
    private static long bytesToLong(byte [] address) {
        long ipnum = 0;
        for (int i = 0; i < 4; ++i) {
            long y = address[i];
            if (y < 0) {
                y+= 256;
            }
            ipnum += y << ((3-i)*8);
        }
        return ipnum;
    }

    private static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }
}
