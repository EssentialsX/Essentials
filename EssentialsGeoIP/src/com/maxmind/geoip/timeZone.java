package com.maxmind.geoip;
// generated automatically from admin/generate_timeZone.pl
public class timeZone {
  static public String timeZoneByCountryAndRegion(String country,String region) {
    String timezone = null;
    if (country == null) {
      return null;
    }
    if (region == null) {
      region = "";
    }
    if (country.equals("US") == true) {
      if (region.equals("AL") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("AK") == true) {
        timezone = "America/Anchorage";
      } else if (region.equals("AZ") == true) {
        timezone = "America/Phoenix";
      } else if (region.equals("AR") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("CA") == true) {
        timezone = "America/Los_Angeles";
      } else if (region.equals("CO") == true) {
        timezone = "America/Denver";
      } else if (region.equals("CT") == true) {
        timezone = "America/New_York";
      } else if (region.equals("DE") == true) {
        timezone = "America/New_York";
      } else if (region.equals("DC") == true) {
        timezone = "America/New_York";
      } else if (region.equals("FL") == true) {
        timezone = "America/New_York";
      } else if (region.equals("GA") == true) {
        timezone = "America/New_York";
      } else if (region.equals("HI") == true) {
        timezone = "Pacific/Honolulu";
      } else if (region.equals("ID") == true) {
        timezone = "America/Denver";
      } else if (region.equals("IL") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("IN") == true) {
        timezone = "America/Indianapolis";
      } else if (region.equals("IA") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("KS") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("KY") == true) {
        timezone = "America/New_York";
      } else if (region.equals("LA") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("ME") == true) {
        timezone = "America/New_York";
      } else if (region.equals("MD") == true) {
        timezone = "America/New_York";
      } else if (region.equals("MA") == true) {
        timezone = "America/New_York";
      } else if (region.equals("MI") == true) {
        timezone = "America/New_York";
      } else if (region.equals("MN") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("MS") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("MO") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("MT") == true) {
        timezone = "America/Denver";
      } else if (region.equals("NE") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("NV") == true) {
        timezone = "America/Los_Angeles";
      } else if (region.equals("NH") == true) {
        timezone = "America/New_York";
      } else if (region.equals("NJ") == true) {
        timezone = "America/New_York";
      } else if (region.equals("NM") == true) {
        timezone = "America/Denver";
      } else if (region.equals("NY") == true) {
        timezone = "America/New_York";
      } else if (region.equals("NC") == true) {
        timezone = "America/New_York";
      } else if (region.equals("ND") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("OH") == true) {
        timezone = "America/New_York";
      } else if (region.equals("OK") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("OR") == true) {
        timezone = "America/Los_Angeles";
      } else if (region.equals("PA") == true) {
        timezone = "America/New_York";
      } else if (region.equals("RI") == true) {
        timezone = "America/New_York";
      } else if (region.equals("SC") == true) {
        timezone = "America/New_York";
      } else if (region.equals("SD") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("TN") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("TX") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("UT") == true) {
        timezone = "America/Denver";
      } else if (region.equals("VT") == true) {
        timezone = "America/New_York";
      } else if (region.equals("VA") == true) {
        timezone = "America/New_York";
      } else if (region.equals("WA") == true) {
        timezone = "America/Los_Angeles";
      } else if (region.equals("WV") == true) {
        timezone = "America/New_York";
      } else if (region.equals("WI") == true) {
        timezone = "America/Chicago";
      } else if (region.equals("WY") == true) {
        timezone = "America/Denver";
      }
    } else if (country.equals("CA") == true) {
      if (region.equals("AB") == true) {
        timezone = "America/Edmonton";
      } else if (region.equals("BC") == true) {
        timezone = "America/Vancouver";
      } else if (region.equals("MB") == true) {
        timezone = "America/Winnipeg";
      } else if (region.equals("NB") == true) {
        timezone = "America/Halifax";
      } else if (region.equals("NL") == true) {
        timezone = "America/St_Johns";
      } else if (region.equals("NT") == true) {
        timezone = "America/Yellowknife";
      } else if (region.equals("NS") == true) {
        timezone = "America/Halifax";
      } else if (region.equals("NU") == true) {
        timezone = "America/Rankin_Inlet";
      } else if (region.equals("ON") == true) {
        timezone = "America/Rainy_River";
      } else if (region.equals("PE") == true) {
        timezone = "America/Halifax";
      } else if (region.equals("QC") == true) {
        timezone = "America/Montreal";
      } else if (region.equals("SK") == true) {
        timezone = "America/Regina";
      } else if (region.equals("YT") == true) {
        timezone = "America/Whitehorse";
      }
    } else if (country.equals("AU") == true) {
      if (region.equals("01") == true) {
        timezone = "Australia/Canberra";
      } else if (region.equals("02") == true) {
        timezone = "Australia/NSW";
      } else if (region.equals("03") == true) {
        timezone = "Australia/North";
      } else if (region.equals("04") == true) {
        timezone = "Australia/Queensland";
      } else if (region.equals("05") == true) {
        timezone = "Australia/South";
      } else if (region.equals("06") == true) {
        timezone = "Australia/Tasmania";
      } else if (region.equals("07") == true) {
        timezone = "Australia/Victoria";
      } else if (region.equals("08") == true) {
        timezone = "Australia/West";
      }
    } else if (country.equals("AS") == true) {
      timezone = "US/Samoa";
    } else if (country.equals("CI") == true) {
      timezone = "Africa/Abidjan";
    } else if (country.equals("GH") == true) {
      timezone = "Africa/Accra";
    } else if (country.equals("DZ") == true) {
      timezone = "Africa/Algiers";
    } else if (country.equals("ER") == true) {
      timezone = "Africa/Asmera";
    } else if (country.equals("ML") == true) {
      timezone = "Africa/Bamako";
    } else if (country.equals("CF") == true) {
      timezone = "Africa/Bangui";
    } else if (country.equals("GM") == true) {
      timezone = "Africa/Banjul";
    } else if (country.equals("GW") == true) {
      timezone = "Africa/Bissau";
    } else if (country.equals("CG") == true) {
      timezone = "Africa/Brazzaville";
    } else if (country.equals("BI") == true) {
      timezone = "Africa/Bujumbura";
    } else if (country.equals("EG") == true) {
      timezone = "Africa/Cairo";
    } else if (country.equals("MA") == true) {
      timezone = "Africa/Casablanca";
    } else if (country.equals("GN") == true) {
      timezone = "Africa/Conakry";
    } else if (country.equals("SN") == true) {
      timezone = "Africa/Dakar";
    } else if (country.equals("DJ") == true) {
      timezone = "Africa/Djibouti";
    } else if (country.equals("SL") == true) {
      timezone = "Africa/Freetown";
    } else if (country.equals("BW") == true) {
      timezone = "Africa/Gaborone";
    } else if (country.equals("ZW") == true) {
      timezone = "Africa/Harare";
    } else if (country.equals("ZA") == true) {
      timezone = "Africa/Johannesburg";
    } else if (country.equals("UG") == true) {
      timezone = "Africa/Kampala";
    } else if (country.equals("SD") == true) {
      timezone = "Africa/Khartoum";
    } else if (country.equals("RW") == true) {
      timezone = "Africa/Kigali";
    } else if (country.equals("NG") == true) {
      timezone = "Africa/Lagos";
    } else if (country.equals("GA") == true) {
      timezone = "Africa/Libreville";
    } else if (country.equals("TG") == true) {
      timezone = "Africa/Lome";
    } else if (country.equals("AO") == true) {
      timezone = "Africa/Luanda";
    } else if (country.equals("ZM") == true) {
      timezone = "Africa/Lusaka";
    } else if (country.equals("GQ") == true) {
      timezone = "Africa/Malabo";
    } else if (country.equals("MZ") == true) {
      timezone = "Africa/Maputo";
    } else if (country.equals("LS") == true) {
      timezone = "Africa/Maseru";
    } else if (country.equals("SZ") == true) {
      timezone = "Africa/Mbabane";
    } else if (country.equals("SO") == true) {
      timezone = "Africa/Mogadishu";
    } else if (country.equals("LR") == true) {
      timezone = "Africa/Monrovia";
    } else if (country.equals("KE") == true) {
      timezone = "Africa/Nairobi";
    } else if (country.equals("TD") == true) {
      timezone = "Africa/Ndjamena";
    } else if (country.equals("NE") == true) {
      timezone = "Africa/Niamey";
    } else if (country.equals("MR") == true) {
      timezone = "Africa/Nouakchott";
    } else if (country.equals("BF") == true) {
      timezone = "Africa/Ouagadougou";
    } else if (country.equals("ST") == true) {
      timezone = "Africa/Sao_Tome";
    } else if (country.equals("LY") == true) {
      timezone = "Africa/Tripoli";
    } else if (country.equals("TN") == true) {
      timezone = "Africa/Tunis";
    } else if (country.equals("AI") == true) {
      timezone = "America/Anguilla";
    } else if (country.equals("AG") == true) {
      timezone = "America/Antigua";
    } else if (country.equals("AW") == true) {
      timezone = "America/Aruba";
    } else if (country.equals("BB") == true) {
      timezone = "America/Barbados";
    } else if (country.equals("BZ") == true) {
      timezone = "America/Belize";
    } else if (country.equals("CO") == true) {
      timezone = "America/Bogota";
    } else if (country.equals("VE") == true) {
      timezone = "America/Caracas";
    } else if (country.equals("KY") == true) {
      timezone = "America/Cayman";
    } else if (country.equals("CR") == true) {
      timezone = "America/Costa_Rica";
    } else if (country.equals("DM") == true) {
      timezone = "America/Dominica";
    } else if (country.equals("SV") == true) {
      timezone = "America/El_Salvador";
    } else if (country.equals("GD") == true) {
      timezone = "America/Grenada";
    } else if (country.equals("FR") == true) {
      timezone = "Europe/Paris";
    } else if (country.equals("GP") == true) {
      timezone = "America/Guadeloupe";
    } else if (country.equals("GT") == true) {
      timezone = "America/Guatemala";
    } else if (country.equals("GY") == true) {
      timezone = "America/Guyana";
    } else if (country.equals("CU") == true) {
      timezone = "America/Havana";
    } else if (country.equals("JM") == true) {
      timezone = "America/Jamaica";
    } else if (country.equals("BO") == true) {
      timezone = "America/La_Paz";
    } else if (country.equals("PE") == true) {
      timezone = "America/Lima";
    } else if (country.equals("NI") == true) {
      timezone = "America/Managua";
    } else if (country.equals("MQ") == true) {
      timezone = "America/Martinique";
    } else if (country.equals("UY") == true) {
      timezone = "America/Montevideo";
    } else if (country.equals("MS") == true) {
      timezone = "America/Montserrat";
    } else if (country.equals("BS") == true) {
      timezone = "America/Nassau";
    } else if (country.equals("PA") == true) {
      timezone = "America/Panama";
    } else if (country.equals("SR") == true) {
      timezone = "America/Paramaribo";
    } else if (country.equals("PR") == true) {
      timezone = "America/Puerto_Rico";
    } else if (country.equals("KN") == true) {
      timezone = "America/St_Kitts";
    } else if (country.equals("LC") == true) {
      timezone = "America/St_Lucia";
    } else if (country.equals("VC") == true) {
      timezone = "America/St_Vincent";
    } else if (country.equals("HN") == true) {
      timezone = "America/Tegucigalpa";
    } else if (country.equals("YE") == true) {
      timezone = "Asia/Aden";
    } else if (country.equals("JO") == true) {
      timezone = "Asia/Amman";
    } else if (country.equals("TM") == true) {
      timezone = "Asia/Ashgabat";
    } else if (country.equals("IQ") == true) {
      timezone = "Asia/Baghdad";
    } else if (country.equals("BH") == true) {
      timezone = "Asia/Bahrain";
    } else if (country.equals("AZ") == true) {
      timezone = "Asia/Baku";
    } else if (country.equals("TH") == true) {
      timezone = "Asia/Bangkok";
    } else if (country.equals("LB") == true) {
      timezone = "Asia/Beirut";
    } else if (country.equals("KG") == true) {
      timezone = "Asia/Bishkek";
    } else if (country.equals("BN") == true) {
      timezone = "Asia/Brunei";
    } else if (country.equals("IN") == true) {
      timezone = "Asia/Calcutta";
    } else if (country.equals("MN") == true) {
      timezone = "Asia/Choibalsan";
    } else if (country.equals("LK") == true) {
      timezone = "Asia/Colombo";
    } else if (country.equals("BD") == true) {
      timezone = "Asia/Dhaka";
    } else if (country.equals("AE") == true) {
      timezone = "Asia/Dubai";
    } else if (country.equals("TJ") == true) {
      timezone = "Asia/Dushanbe";
    } else if (country.equals("HK") == true) {
      timezone = "Asia/Hong_Kong";
    } else if (country.equals("TR") == true) {
      timezone = "Asia/Istanbul";
    } else if (country.equals("IL") == true) {
      timezone = "Asia/Jerusalem";
    } else if (country.equals("AF") == true) {
      timezone = "Asia/Kabul";
    } else if (country.equals("PK") == true) {
      timezone = "Asia/Karachi";
    } else if (country.equals("NP") == true) {
      timezone = "Asia/Katmandu";
    } else if (country.equals("KW") == true) {
      timezone = "Asia/Kuwait";
    } else if (country.equals("MO") == true) {
      timezone = "Asia/Macao";
    } else if (country.equals("PH") == true) {
      timezone = "Asia/Manila";
    } else if (country.equals("OM") == true) {
      timezone = "Asia/Muscat";
    } else if (country.equals("CY") == true) {
      timezone = "Asia/Nicosia";
    } else if (country.equals("KP") == true) {
      timezone = "Asia/Pyongyang";
    } else if (country.equals("QA") == true) {
      timezone = "Asia/Qatar";
    } else if (country.equals("MM") == true) {
      timezone = "Asia/Rangoon";
    } else if (country.equals("SA") == true) {
      timezone = "Asia/Riyadh";
    } else if (country.equals("KR") == true) {
      timezone = "Asia/Seoul";
    } else if (country.equals("SG") == true) {
      timezone = "Asia/Singapore";
    } else if (country.equals("TW") == true) {
      timezone = "Asia/Taipei";
    } else if (country.equals("GE") == true) {
      timezone = "Asia/Tbilisi";
    } else if (country.equals("BT") == true) {
      timezone = "Asia/Thimphu";
    } else if (country.equals("JP") == true) {
      timezone = "Asia/Tokyo";
    } else if (country.equals("LA") == true) {
      timezone = "Asia/Vientiane";
    } else if (country.equals("AM") == true) {
      timezone = "Asia/Yerevan";
    } else if (country.equals("BM") == true) {
      timezone = "Atlantic/Bermuda";
    } else if (country.equals("CV") == true) {
      timezone = "Atlantic/Cape_Verde";
    } else if (country.equals("FO") == true) {
      timezone = "Atlantic/Faeroe";
    } else if (country.equals("IS") == true) {
      timezone = "Atlantic/Reykjavik";
    } else if (country.equals("GS") == true) {
      timezone = "Atlantic/South_Georgia";
    } else if (country.equals("SH") == true) {
      timezone = "Atlantic/St_Helena";
    } else if (country.equals("CL") == true) {
      timezone = "Chile/Continental";
    } else if (country.equals("NL") == true) {
      timezone = "Europe/Amsterdam";
    } else if (country.equals("AD") == true) {
      timezone = "Europe/Andorra";
    } else if (country.equals("GR") == true) {
      timezone = "Europe/Athens";
    } else if (country.equals("YU") == true) {
      timezone = "Europe/Belgrade";
    } else if (country.equals("DE") == true) {
      timezone = "Europe/Berlin";
    } else if (country.equals("SK") == true) {
      timezone = "Europe/Bratislava";
    } else if (country.equals("BE") == true) {
      timezone = "Europe/Brussels";
    } else if (country.equals("RO") == true) {
      timezone = "Europe/Bucharest";
    } else if (country.equals("HU") == true) {
      timezone = "Europe/Budapest";
    } else if (country.equals("DK") == true) {
      timezone = "Europe/Copenhagen";
    } else if (country.equals("IE") == true) {
      timezone = "Europe/Dublin";
    } else if (country.equals("GI") == true) {
      timezone = "Europe/Gibraltar";
    } else if (country.equals("FI") == true) {
      timezone = "Europe/Helsinki";
    } else if (country.equals("SI") == true) {
      timezone = "Europe/Ljubljana";
    } else if (country.equals("GB") == true) {
      timezone = "Europe/London";
    } else if (country.equals("LU") == true) {
      timezone = "Europe/Luxembourg";
    } else if (country.equals("MT") == true) {
      timezone = "Europe/Malta";
    } else if (country.equals("BY") == true) {
      timezone = "Europe/Minsk";
    } else if (country.equals("MC") == true) {
      timezone = "Europe/Monaco";
    } else if (country.equals("NO") == true) {
      timezone = "Europe/Oslo";
    } else if (country.equals("CZ") == true) {
      timezone = "Europe/Prague";
    } else if (country.equals("LV") == true) {
      timezone = "Europe/Riga";
    } else if (country.equals("IT") == true) {
      timezone = "Europe/Rome";
    } else if (country.equals("SM") == true) {
      timezone = "Europe/San_Marino";
    } else if (country.equals("BA") == true) {
      timezone = "Europe/Sarajevo";
    } else if (country.equals("MK") == true) {
      timezone = "Europe/Skopje";
    } else if (country.equals("BG") == true) {
      timezone = "Europe/Sofia";
    } else if (country.equals("SE") == true) {
      timezone = "Europe/Stockholm";
    } else if (country.equals("EE") == true) {
      timezone = "Europe/Tallinn";
    } else if (country.equals("AL") == true) {
      timezone = "Europe/Tirane";
    } else if (country.equals("LI") == true) {
      timezone = "Europe/Vaduz";
    } else if (country.equals("VA") == true) {
      timezone = "Europe/Vatican";
    } else if (country.equals("AT") == true) {
      timezone = "Europe/Vienna";
    } else if (country.equals("LT") == true) {
      timezone = "Europe/Vilnius";
    } else if (country.equals("PL") == true) {
      timezone = "Europe/Warsaw";
    } else if (country.equals("HR") == true) {
      timezone = "Europe/Zagreb";
    } else if (country.equals("IR") == true) {
      timezone = "Asia/Tehran";
    } else if (country.equals("MG") == true) {
      timezone = "Indian/Antananarivo";
    } else if (country.equals("CX") == true) {
      timezone = "Indian/Christmas";
    } else if (country.equals("CC") == true) {
      timezone = "Indian/Cocos";
    } else if (country.equals("KM") == true) {
      timezone = "Indian/Comoro";
    } else if (country.equals("MV") == true) {
      timezone = "Indian/Maldives";
    } else if (country.equals("MU") == true) {
      timezone = "Indian/Mauritius";
    } else if (country.equals("YT") == true) {
      timezone = "Indian/Mayotte";
    } else if (country.equals("RE") == true) {
      timezone = "Indian/Reunion";
    } else if (country.equals("FJ") == true) {
      timezone = "Pacific/Fiji";
    } else if (country.equals("TV") == true) {
      timezone = "Pacific/Funafuti";
    } else if (country.equals("GU") == true) {
      timezone = "Pacific/Guam";
    } else if (country.equals("NR") == true) {
      timezone = "Pacific/Nauru";
    } else if (country.equals("NU") == true) {
      timezone = "Pacific/Niue";
    } else if (country.equals("NF") == true) {
      timezone = "Pacific/Norfolk";
    } else if (country.equals("PW") == true) {
      timezone = "Pacific/Palau";
    } else if (country.equals("PN") == true) {
      timezone = "Pacific/Pitcairn";
    } else if (country.equals("CK") == true) {
      timezone = "Pacific/Rarotonga";
    } else if (country.equals("WS") == true) {
      timezone = "Pacific/Samoa";
    } else if (country.equals("KI") == true) {
      timezone = "Pacific/Tarawa";
    } else if (country.equals("TO") == true) {
      timezone = "Pacific/Tongatapu";
    } else if (country.equals("WF") == true) {
      timezone = "Pacific/Wallis";
    } else if (country.equals("TZ") == true) {
      timezone = "Africa/Dar_es_Salaam";
    } else if (country.equals("VN") == true) {
      timezone = "Asia/Phnom_Penh";
    } else if (country.equals("KH") == true) {
      timezone = "Asia/Phnom_Penh";
    } else if (country.equals("CM") == true) {
      timezone = "Africa/Lagos";
    } else if (country.equals("DO") == true) {
      timezone = "America/Santo_Domingo";
    } else if (country.equals("ET") == true) {
      timezone = "Africa/Addis_Ababa";
    } else if (country.equals("FX") == true) {
      timezone = "Europe/Paris";
    } else if (country.equals("HT") == true) {
      timezone = "America/Port-au-Prince";
    } else if (country.equals("CH") == true) {
      timezone = "Europe/Zurich";
    } else if (country.equals("AN") == true) {
      timezone = "America/Curacao";
    } else if (country.equals("BJ") == true) {
      timezone = "Africa/Porto-Novo";
    } else if (country.equals("EH") == true) {
      timezone = "Africa/El_Aaiun";
    } else if (country.equals("FK") == true) {
      timezone = "Atlantic/Stanley";
    } else if (country.equals("GF") == true) {
      timezone = "America/Cayenne";
    } else if (country.equals("IO") == true) {
      timezone = "Indian/Chagos";
    } else if (country.equals("MD") == true) {
      timezone = "Europe/Chisinau";
    } else if (country.equals("MP") == true) {
      timezone = "Pacific/Saipan";
    } else if (country.equals("MW") == true) {
      timezone = "Africa/Blantyre";
    } else if (country.equals("NA") == true) {
      timezone = "Africa/Windhoek";
    } else if (country.equals("NC") == true) {
      timezone = "Pacific/Noumea";
    } else if (country.equals("PG") == true) {
      timezone = "Pacific/Port_Moresby";
    } else if (country.equals("PM") == true) {
      timezone = "America/Miquelon";
    } else if (country.equals("PS") == true) {
      timezone = "Asia/Gaza";
    } else if (country.equals("PY") == true) {
      timezone = "America/Asuncion";
    } else if (country.equals("SB") == true) {
      timezone = "Pacific/Guadalcanal";
    } else if (country.equals("SC") == true) {
      timezone = "Indian/Mahe";
    } else if (country.equals("SJ") == true) {
      timezone = "Arctic/Longyearbyen";
    } else if (country.equals("SY") == true) {
      timezone = "Asia/Damascus";
    } else if (country.equals("TC") == true) {
      timezone = "America/Grand_Turk";
    } else if (country.equals("TF") == true) {
      timezone = "Indian/Kerguelen";
    } else if (country.equals("TK") == true) {
      timezone = "Pacific/Fakaofo";
    } else if (country.equals("TT") == true) {
      timezone = "America/Port_of_Spain";
    } else if (country.equals("VG") == true) {
      timezone = "America/Tortola";
    } else if (country.equals("VI") == true) {
      timezone = "America/St_Thomas";
    } else if (country.equals("VU") == true) {
      timezone = "Pacific/Efate";
    } else if (country.equals("RS") == true) {
      timezone = "Europe/Belgrade";
    } else if (country.equals("ME") == true) {
      timezone = "Europe/Podgorica";
    } else if (country.equals("AX") == true) {
      timezone = "Europe/Mariehamn";
    } else if (country.equals("GG") == true) {
      timezone = "Europe/Guernsey";
    } else if (country.equals("IM") == true) {
      timezone = "Europe/Isle_of_Man";
    } else if (country.equals("JE") == true) {
      timezone = "Europe/Jersey";
    } else if (country.equals("BL") == true) {
      timezone = "America/St_Barthelemy";
    } else if (country.equals("MF") == true) {
      timezone = "America/Marigot";
    } else if (country.equals("AR") == true) {
      if (region.equals("01") == true) {
        timezone = "America/Argentina/Buenos_Aires";
      } else if (region.equals("02") == true) {
        timezone = "America/Argentina/Catamarca";
      } else if (region.equals("03") == true) {
        timezone = "America/Argentina/Tucuman";
      } else if (region.equals("04") == true) {
        timezone = "America/Argentina/Rio_Gallegos";
      } else if (region.equals("05") == true) {
        timezone = "America/Argentina/Cordoba";
      } else if (region.equals("06") == true) {
        timezone = "America/Argentina/Tucuman";
      } else if (region.equals("07") == true) {
        timezone = "America/Argentina/Buenos_Aires";
      } else if (region.equals("08") == true) {
        timezone = "America/Argentina/Buenos_Aires";
      } else if (region.equals("09") == true) {
        timezone = "America/Argentina/Tucuman";
      } else if (region.equals("10") == true) {
        timezone = "America/Argentina/Jujuy";
      } else if (region.equals("11") == true) {
        timezone = "America/Argentina/San_Luis";
      } else if (region.equals("12") == true) {
        timezone = "America/Argentina/La_Rioja";
      } else if (region.equals("13") == true) {
        timezone = "America/Argentina/Mendoza";
      } else if (region.equals("14") == true) {
        timezone = "America/Argentina/Buenos_Aires";
      } else if (region.equals("15") == true) {
        timezone = "America/Argentina/San_Luis";
      } else if (region.equals("16") == true) {
        timezone = "America/Argentina/Buenos_Aires";
      } else if (region.equals("17") == true) {
        timezone = "America/Argentina/Salta";
      } else if (region.equals("18") == true) {
        timezone = "America/Argentina/San_Juan";
      } else if (region.equals("19") == true) {
        timezone = "America/Argentina/San_Luis";
      } else if (region.equals("20") == true) {
        timezone = "America/Argentina/Rio_Gallegos";
      } else if (region.equals("21") == true) {
        timezone = "America/Argentina/Buenos_Aires";
      } else if (region.equals("22") == true) {
        timezone = "America/Argentina/Catamarca";
      } else if (region.equals("23") == true) {
        timezone = "America/Argentina/Ushuaia";
      } else if (region.equals("24") == true) {
        timezone = "America/Argentina/Tucuman";
      }
    } else if (country.equals("BR") == true) {
      if (region.equals("01") == true) {
        timezone = "America/Rio_Branco";
      } else if (region.equals("02") == true) {
        timezone = "America/Maceio";
      } else if (region.equals("03") == true) {
        timezone = "America/Sao_Paulo";
      } else if (region.equals("04") == true) {
        timezone = "America/Manaus";
      } else if (region.equals("05") == true) {
        timezone = "America/Bahia";
      } else if (region.equals("06") == true) {
        timezone = "America/Fortaleza";
      } else if (region.equals("07") == true) {
        timezone = "America/Sao_Paulo";
      } else if (region.equals("08") == true) {
        timezone = "America/Sao_Paulo";
      } else if (region.equals("11") == true) {
        timezone = "America/Campo_Grande";
      } else if (region.equals("13") == true) {
        timezone = "America/Belem";
      } else if (region.equals("14") == true) {
        timezone = "America/Cuiaba";
      } else if (region.equals("15") == true) {
        timezone = "America/Sao_Paulo";
      } else if (region.equals("16") == true) {
        timezone = "America/Belem";
      } else if (region.equals("17") == true) {
        timezone = "America/Recife";
      } else if (region.equals("18") == true) {
        timezone = "America/Sao_Paulo";
      } else if (region.equals("20") == true) {
        timezone = "America/Fortaleza";
      } else if (region.equals("21") == true) {
        timezone = "America/Sao_Paulo";
      } else if (region.equals("22") == true) {
        timezone = "America/Recife";
      } else if (region.equals("23") == true) {
        timezone = "America/Sao_Paulo";
      } else if (region.equals("24") == true) {
        timezone = "America/Porto_Velho";
      } else if (region.equals("25") == true) {
        timezone = "America/Boa_Vista";
      } else if (region.equals("26") == true) {
        timezone = "America/Sao_Paulo";
      } else if (region.equals("27") == true) {
        timezone = "America/Sao_Paulo";
      } else if (region.equals("28") == true) {
        timezone = "America/Maceio";
      } else if (region.equals("29") == true) {
        timezone = "America/Sao_Paulo";
      } else if (region.equals("30") == true) {
        timezone = "America/Recife";
      } else if (region.equals("31") == true) {
        timezone = "America/Araguaina";
      }
    } else if (country.equals("CD") == true) {
      if (region.equals("02") == true) {
        timezone = "Africa/Kinshasa";
      } else if (region.equals("05") == true) {
        timezone = "Africa/Lubumbashi";
      } else if (region.equals("06") == true) {
        timezone = "Africa/Kinshasa";
      } else if (region.equals("08") == true) {
        timezone = "Africa/Kinshasa";
      } else if (region.equals("10") == true) {
        timezone = "Africa/Lubumbashi";
      } else if (region.equals("11") == true) {
        timezone = "Africa/Lubumbashi";
      } else if (region.equals("12") == true) {
        timezone = "Africa/Lubumbashi";
      }
    } else if (country.equals("CN") == true) {
      if (region.equals("01") == true) {
        timezone = "Asia/Shanghai";
      } else if (region.equals("02") == true) {
        timezone = "Asia/Shanghai";
      } else if (region.equals("03") == true) {
        timezone = "Asia/Shanghai";
      } else if (region.equals("04") == true) {
        timezone = "Asia/Shanghai";
      } else if (region.equals("05") == true) {
        timezone = "Asia/Harbin";
      } else if (region.equals("06") == true) {
        timezone = "Asia/Chongqing";
      } else if (region.equals("07") == true) {
        timezone = "Asia/Shanghai";
      } else if (region.equals("08") == true) {
        timezone = "Asia/Harbin";
      } else if (region.equals("09") == true) {
        timezone = "Asia/Shanghai";
      } else if (region.equals("10") == true) {
        timezone = "Asia/Shanghai";
      } else if (region.equals("11") == true) {
        timezone = "Asia/Chongqing";
      } else if (region.equals("12") == true) {
        timezone = "Asia/Shanghai";
      } else if (region.equals("13") == true) {
        timezone = "Asia/Urumqi";
      } else if (region.equals("14") == true) {
        timezone = "Asia/Chongqing";
      } else if (region.equals("15") == true) {
        timezone = "Asia/Chongqing";
      } else if (region.equals("16") == true) {
        timezone = "Asia/Chongqing";
      } else if (region.equals("18") == true) {
        timezone = "Asia/Chongqing";
      } else if (region.equals("19") == true) {
        timezone = "Asia/Harbin";
      } else if (region.equals("20") == true) {
        timezone = "Asia/Harbin";
      } else if (region.equals("21") == true) {
        timezone = "Asia/Chongqing";
      } else if (region.equals("22") == true) {
        timezone = "Asia/Harbin";
      } else if (region.equals("23") == true) {
        timezone = "Asia/Shanghai";
      } else if (region.equals("24") == true) {
        timezone = "Asia/Chongqing";
      } else if (region.equals("25") == true) {
        timezone = "Asia/Shanghai";
      } else if (region.equals("26") == true) {
        timezone = "Asia/Chongqing";
      } else if (region.equals("28") == true) {
        timezone = "Asia/Shanghai";
      } else if (region.equals("29") == true) {
        timezone = "Asia/Chongqing";
      } else if (region.equals("30") == true) {
        timezone = "Asia/Chongqing";
      } else if (region.equals("31") == true) {
        timezone = "Asia/Chongqing";
      } else if (region.equals("32") == true) {
        timezone = "Asia/Chongqing";
      } else if (region.equals("33") == true) {
        timezone = "Asia/Chongqing";
      }
    } else if (country.equals("EC") == true) {
      if (region.equals("01") == true) {
        timezone = "Pacific/Galapagos";
      } else if (region.equals("02") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("03") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("04") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("05") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("06") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("07") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("08") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("09") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("10") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("11") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("12") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("13") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("14") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("15") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("17") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("18") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("19") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("20") == true) {
        timezone = "America/Guayaquil";
      } else if (region.equals("22") == true) {
        timezone = "America/Guayaquil";
      }
    } else if (country.equals("ES") == true) {
      if (region.equals("07") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("27") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("29") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("31") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("32") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("34") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("39") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("51") == true) {
        timezone = "Africa/Ceuta";
      } else if (region.equals("52") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("53") == true) {
        timezone = "Atlantic/Canary";
      } else if (region.equals("54") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("55") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("56") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("57") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("58") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("59") == true) {
        timezone = "Europe/Madrid";
      } else if (region.equals("60") == true) {
        timezone = "Europe/Madrid";
      }
    } else if (country.equals("GL") == true) {
      if (region.equals("01") == true) {
        timezone = "America/Thule";
      } else if (region.equals("02") == true) {
        timezone = "America/Godthab";
      } else if (region.equals("03") == true) {
        timezone = "America/Godthab";
      }
    } else if (country.equals("ID") == true) {
      if (region.equals("01") == true) {
        timezone = "Asia/Pontianak";
      } else if (region.equals("02") == true) {
        timezone = "Asia/Makassar";
      } else if (region.equals("03") == true) {
        timezone = "Asia/Jakarta";
      } else if (region.equals("04") == true) {
        timezone = "Asia/Jakarta";
      } else if (region.equals("05") == true) {
        timezone = "Asia/Jakarta";
      } else if (region.equals("06") == true) {
        timezone = "Asia/Jakarta";
      } else if (region.equals("07") == true) {
        timezone = "Asia/Jakarta";
      } else if (region.equals("08") == true) {
        timezone = "Asia/Jakarta";
      } else if (region.equals("09") == true) {
        timezone = "Asia/Jayapura";
      } else if (region.equals("10") == true) {
        timezone = "Asia/Jakarta";
      } else if (region.equals("11") == true) {
        timezone = "Asia/Pontianak";
      } else if (region.equals("12") == true) {
        timezone = "Asia/Makassar";
      } else if (region.equals("13") == true) {
        timezone = "Asia/Makassar";
      } else if (region.equals("14") == true) {
        timezone = "Asia/Makassar";
      } else if (region.equals("15") == true) {
        timezone = "Asia/Jakarta";
      } else if (region.equals("16") == true) {
        timezone = "Asia/Makassar";
      } else if (region.equals("17") == true) {
        timezone = "Asia/Makassar";
      } else if (region.equals("18") == true) {
        timezone = "Asia/Makassar";
      } else if (region.equals("19") == true) {
        timezone = "Asia/Pontianak";
      } else if (region.equals("20") == true) {
        timezone = "Asia/Makassar";
      } else if (region.equals("21") == true) {
        timezone = "Asia/Makassar";
      } else if (region.equals("22") == true) {
        timezone = "Asia/Makassar";
      } else if (region.equals("23") == true) {
        timezone = "Asia/Makassar";
      } else if (region.equals("24") == true) {
        timezone = "Asia/Jakarta";
      } else if (region.equals("25") == true) {
        timezone = "Asia/Pontianak";
      } else if (region.equals("26") == true) {
        timezone = "Asia/Pontianak";
      } else if (region.equals("30") == true) {
        timezone = "Asia/Jakarta";
      } else if (region.equals("31") == true) {
        timezone = "Asia/Makassar";
      } else if (region.equals("33") == true) {
        timezone = "Asia/Jakarta";
      }
    } else if (country.equals("KZ") == true) {
      if (region.equals("01") == true) {
        timezone = "Asia/Almaty";
      } else if (region.equals("02") == true) {
        timezone = "Asia/Almaty";
      } else if (region.equals("03") == true) {
        timezone = "Asia/Qyzylorda";
      } else if (region.equals("04") == true) {
        timezone = "Asia/Aqtobe";
      } else if (region.equals("05") == true) {
        timezone = "Asia/Qyzylorda";
      } else if (region.equals("06") == true) {
        timezone = "Asia/Aqtau";
      } else if (region.equals("07") == true) {
        timezone = "Asia/Oral";
      } else if (region.equals("08") == true) {
        timezone = "Asia/Qyzylorda";
      } else if (region.equals("09") == true) {
        timezone = "Asia/Aqtau";
      } else if (region.equals("10") == true) {
        timezone = "Asia/Qyzylorda";
      } else if (region.equals("11") == true) {
        timezone = "Asia/Almaty";
      } else if (region.equals("12") == true) {
        timezone = "Asia/Qyzylorda";
      } else if (region.equals("13") == true) {
        timezone = "Asia/Aqtobe";
      } else if (region.equals("14") == true) {
        timezone = "Asia/Qyzylorda";
      } else if (region.equals("15") == true) {
        timezone = "Asia/Almaty";
      } else if (region.equals("16") == true) {
        timezone = "Asia/Aqtobe";
      } else if (region.equals("17") == true) {
        timezone = "Asia/Almaty";
      }
    } else if (country.equals("MX") == true) {
      if (region.equals("01") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("02") == true) {
        timezone = "America/Tijuana";
      } else if (region.equals("03") == true) {
        timezone = "America/Hermosillo";
      } else if (region.equals("04") == true) {
        timezone = "America/Merida";
      } else if (region.equals("05") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("06") == true) {
        timezone = "America/Chihuahua";
      } else if (region.equals("07") == true) {
        timezone = "America/Monterrey";
      } else if (region.equals("08") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("09") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("10") == true) {
        timezone = "America/Mazatlan";
      } else if (region.equals("11") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("12") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("13") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("14") == true) {
        timezone = "America/Mazatlan";
      } else if (region.equals("15") == true) {
        timezone = "America/Chihuahua";
      } else if (region.equals("16") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("17") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("18") == true) {
        timezone = "America/Mazatlan";
      } else if (region.equals("19") == true) {
        timezone = "America/Monterrey";
      } else if (region.equals("20") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("21") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("22") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("23") == true) {
        timezone = "America/Cancun";
      } else if (region.equals("24") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("25") == true) {
        timezone = "America/Mazatlan";
      } else if (region.equals("26") == true) {
        timezone = "America/Hermosillo";
      } else if (region.equals("27") == true) {
        timezone = "America/Merida";
      } else if (region.equals("28") == true) {
        timezone = "America/Monterrey";
      } else if (region.equals("29") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("30") == true) {
        timezone = "America/Mexico_City";
      } else if (region.equals("31") == true) {
        timezone = "America/Merida";
      } else if (region.equals("32") == true) {
        timezone = "America/Monterrey";
      }
    } else if (country.equals("MY") == true) {
      if (region.equals("01") == true) {
        timezone = "Asia/Kuala_Lumpur";
      } else if (region.equals("02") == true) {
        timezone = "Asia/Kuala_Lumpur";
      } else if (region.equals("03") == true) {
        timezone = "Asia/Kuala_Lumpur";
      } else if (region.equals("04") == true) {
        timezone = "Asia/Kuala_Lumpur";
      } else if (region.equals("05") == true) {
        timezone = "Asia/Kuala_Lumpur";
      } else if (region.equals("06") == true) {
        timezone = "Asia/Kuala_Lumpur";
      } else if (region.equals("07") == true) {
        timezone = "Asia/Kuala_Lumpur";
      } else if (region.equals("08") == true) {
        timezone = "Asia/Kuala_Lumpur";
      } else if (region.equals("09") == true) {
        timezone = "Asia/Kuala_Lumpur";
      } else if (region.equals("11") == true) {
        timezone = "Asia/Kuching";
      } else if (region.equals("12") == true) {
        timezone = "Asia/Kuala_Lumpur";
      } else if (region.equals("13") == true) {
        timezone = "Asia/Kuala_Lumpur";
      } else if (region.equals("14") == true) {
        timezone = "Asia/Kuala_Lumpur";
      } else if (region.equals("15") == true) {
        timezone = "Asia/Kuching";
      } else if (region.equals("16") == true) {
        timezone = "Asia/Kuching";
      }
    } else if (country.equals("NZ") == true) {
      if (region.equals("85") == true) {
        timezone = "Pacific/Auckland";
      } else if (region.equals("E7") == true) {
        timezone = "Pacific/Auckland";
      } else if (region.equals("E8") == true) {
        timezone = "Pacific/Auckland";
      } else if (region.equals("E9") == true) {
        timezone = "Pacific/Auckland";
      } else if (region.equals("F1") == true) {
        timezone = "Pacific/Auckland";
      } else if (region.equals("F2") == true) {
        timezone = "Pacific/Auckland";
      } else if (region.equals("F3") == true) {
        timezone = "Pacific/Auckland";
      } else if (region.equals("F4") == true) {
        timezone = "Pacific/Auckland";
      } else if (region.equals("F5") == true) {
        timezone = "Pacific/Auckland";
      } else if (region.equals("F7") == true) {
        timezone = "Pacific/Chatham";
      } else if (region.equals("F8") == true) {
        timezone = "Pacific/Auckland";
      } else if (region.equals("F9") == true) {
        timezone = "Pacific/Auckland";
      } else if (region.equals("G1") == true) {
        timezone = "Pacific/Auckland";
      } else if (region.equals("G2") == true) {
        timezone = "Pacific/Auckland";
      } else if (region.equals("G3") == true) {
        timezone = "Pacific/Auckland";
      }
    } else if (country.equals("PT") == true) {
      if (region.equals("02") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("03") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("04") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("05") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("06") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("07") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("08") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("09") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("10") == true) {
        timezone = "Atlantic/Madeira";
      } else if (region.equals("11") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("13") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("14") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("16") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("17") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("18") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("19") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("20") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("21") == true) {
        timezone = "Europe/Lisbon";
      } else if (region.equals("22") == true) {
        timezone = "Europe/Lisbon";
      }
    } else if (country.equals("RU") == true) {
      if (region.equals("01") == true) {
        timezone = "Europe/Volgograd";
      } else if (region.equals("02") == true) {
        timezone = "Asia/Irkutsk";
      } else if (region.equals("03") == true) {
        timezone = "Asia/Novokuznetsk";
      } else if (region.equals("04") == true) {
        timezone = "Asia/Novosibirsk";
      } else if (region.equals("05") == true) {
        timezone = "Asia/Vladivostok";
      } else if (region.equals("06") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("07") == true) {
        timezone = "Europe/Volgograd";
      } else if (region.equals("08") == true) {
        timezone = "Europe/Samara";
      } else if (region.equals("09") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("10") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("11") == true) {
        timezone = "Asia/Irkutsk";
      } else if (region.equals("13") == true) {
        timezone = "Asia/Yekaterinburg";
      } else if (region.equals("14") == true) {
        timezone = "Asia/Irkutsk";
      } else if (region.equals("15") == true) {
        timezone = "Asia/Anadyr";
      } else if (region.equals("16") == true) {
        timezone = "Europe/Samara";
      } else if (region.equals("17") == true) {
        timezone = "Europe/Volgograd";
      } else if (region.equals("18") == true) {
        timezone = "Asia/Krasnoyarsk";
      } else if (region.equals("20") == true) {
        timezone = "Asia/Irkutsk";
      } else if (region.equals("21") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("22") == true) {
        timezone = "Europe/Volgograd";
      } else if (region.equals("23") == true) {
        timezone = "Europe/Kaliningrad";
      } else if (region.equals("24") == true) {
        timezone = "Europe/Volgograd";
      } else if (region.equals("25") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("26") == true) {
        timezone = "Asia/Kamchatka";
      } else if (region.equals("27") == true) {
        timezone = "Europe/Volgograd";
      } else if (region.equals("28") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("29") == true) {
        timezone = "Asia/Novokuznetsk";
      } else if (region.equals("30") == true) {
        timezone = "Asia/Vladivostok";
      } else if (region.equals("31") == true) {
        timezone = "Asia/Krasnoyarsk";
      } else if (region.equals("32") == true) {
        timezone = "Asia/Omsk";
      } else if (region.equals("33") == true) {
        timezone = "Asia/Yekaterinburg";
      } else if (region.equals("34") == true) {
        timezone = "Asia/Yekaterinburg";
      } else if (region.equals("35") == true) {
        timezone = "Asia/Yekaterinburg";
      } else if (region.equals("36") == true) {
        timezone = "Asia/Anadyr";
      } else if (region.equals("37") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("38") == true) {
        timezone = "Europe/Volgograd";
      } else if (region.equals("39") == true) {
        timezone = "Asia/Krasnoyarsk";
      } else if (region.equals("40") == true) {
        timezone = "Asia/Yekaterinburg";
      } else if (region.equals("41") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("42") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("43") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("44") == true) {
        timezone = "Asia/Magadan";
      } else if (region.equals("45") == true) {
        timezone = "Europe/Samara";
      } else if (region.equals("46") == true) {
        timezone = "Europe/Samara";
      } else if (region.equals("47") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("48") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("49") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("50") == true) {
        timezone = "Asia/Yekaterinburg";
      } else if (region.equals("51") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("52") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("53") == true) {
        timezone = "Asia/Novosibirsk";
      } else if (region.equals("54") == true) {
        timezone = "Asia/Omsk";
      } else if (region.equals("55") == true) {
        timezone = "Europe/Samara";
      } else if (region.equals("56") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("57") == true) {
        timezone = "Europe/Samara";
      } else if (region.equals("58") == true) {
        timezone = "Asia/Yekaterinburg";
      } else if (region.equals("59") == true) {
        timezone = "Asia/Vladivostok";
      } else if (region.equals("60") == true) {
        timezone = "Europe/Kaliningrad";
      } else if (region.equals("61") == true) {
        timezone = "Europe/Volgograd";
      } else if (region.equals("62") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("63") == true) {
        timezone = "Asia/Yakutsk";
      } else if (region.equals("64") == true) {
        timezone = "Asia/Sakhalin";
      } else if (region.equals("65") == true) {
        timezone = "Europe/Samara";
      } else if (region.equals("66") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("67") == true) {
        timezone = "Europe/Samara";
      } else if (region.equals("68") == true) {
        timezone = "Europe/Volgograd";
      } else if (region.equals("69") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("70") == true) {
        timezone = "Europe/Volgograd";
      } else if (region.equals("71") == true) {
        timezone = "Asia/Yekaterinburg";
      } else if (region.equals("72") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("73") == true) {
        timezone = "Europe/Samara";
      } else if (region.equals("74") == true) {
        timezone = "Asia/Krasnoyarsk";
      } else if (region.equals("75") == true) {
        timezone = "Asia/Novosibirsk";
      } else if (region.equals("76") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("77") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("78") == true) {
        timezone = "Asia/Yekaterinburg";
      } else if (region.equals("79") == true) {
        timezone = "Asia/Irkutsk";
      } else if (region.equals("80") == true) {
        timezone = "Asia/Yekaterinburg";
      } else if (region.equals("81") == true) {
        timezone = "Europe/Samara";
      } else if (region.equals("82") == true) {
        timezone = "Asia/Irkutsk";
      } else if (region.equals("83") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("84") == true) {
        timezone = "Europe/Volgograd";
      } else if (region.equals("85") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("86") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("87") == true) {
        timezone = "Asia/Novosibirsk";
      } else if (region.equals("88") == true) {
        timezone = "Europe/Moscow";
      } else if (region.equals("89") == true) {
        timezone = "Asia/Vladivostok";
      }
    } else if (country.equals("UA") == true) {
      if (region.equals("01") == true) {
        timezone = "Europe/Kiev";
      } else if (region.equals("02") == true) {
        timezone = "Europe/Kiev";
      } else if (region.equals("03") == true) {
        timezone = "Europe/Uzhgorod";
      } else if (region.equals("04") == true) {
        timezone = "Europe/Zaporozhye";
      } else if (region.equals("05") == true) {
        timezone = "Europe/Zaporozhye";
      } else if (region.equals("06") == true) {
        timezone = "Europe/Uzhgorod";
      } else if (region.equals("07") == true) {
        timezone = "Europe/Zaporozhye";
      } else if (region.equals("08") == true) {
        timezone = "Europe/Simferopol";
      } else if (region.equals("09") == true) {
        timezone = "Europe/Kiev";
      } else if (region.equals("10") == true) {
        timezone = "Europe/Zaporozhye";
      } else if (region.equals("11") == true) {
        timezone = "Europe/Simferopol";
      } else if (region.equals("13") == true) {
        timezone = "Europe/Kiev";
      } else if (region.equals("14") == true) {
        timezone = "Europe/Zaporozhye";
      } else if (region.equals("15") == true) {
        timezone = "Europe/Uzhgorod";
      } else if (region.equals("16") == true) {
        timezone = "Europe/Zaporozhye";
      } else if (region.equals("17") == true) {
        timezone = "Europe/Simferopol";
      } else if (region.equals("18") == true) {
        timezone = "Europe/Zaporozhye";
      } else if (region.equals("19") == true) {
        timezone = "Europe/Kiev";
      } else if (region.equals("20") == true) {
        timezone = "Europe/Simferopol";
      } else if (region.equals("21") == true) {
        timezone = "Europe/Kiev";
      } else if (region.equals("22") == true) {
        timezone = "Europe/Uzhgorod";
      } else if (region.equals("23") == true) {
        timezone = "Europe/Kiev";
      } else if (region.equals("24") == true) {
        timezone = "Europe/Uzhgorod";
      } else if (region.equals("25") == true) {
        timezone = "Europe/Uzhgorod";
      } else if (region.equals("26") == true) {
        timezone = "Europe/Zaporozhye";
      } else if (region.equals("27") == true) {
        timezone = "Europe/Kiev";
      }
    } else if (country.equals("UZ") == true) {
      if (region.equals("01") == true) {
        timezone = "Asia/Tashkent";
      } else if (region.equals("02") == true) {
        timezone = "Asia/Samarkand";
      } else if (region.equals("03") == true) {
        timezone = "Asia/Tashkent";
      } else if (region.equals("06") == true) {
        timezone = "Asia/Tashkent";
      } else if (region.equals("07") == true) {
        timezone = "Asia/Samarkand";
      } else if (region.equals("08") == true) {
        timezone = "Asia/Samarkand";
      } else if (region.equals("09") == true) {
        timezone = "Asia/Samarkand";
      } else if (region.equals("10") == true) {
        timezone = "Asia/Samarkand";
      } else if (region.equals("12") == true) {
        timezone = "Asia/Samarkand";
      } else if (region.equals("13") == true) {
        timezone = "Asia/Tashkent";
      } else if (region.equals("14") == true) {
        timezone = "Asia/Tashkent";
      }
    } else if (country.equals("TL") == true) {
      timezone = "Asia/Dili";
    } else if (country.equals("PF") == true) {
      timezone = "Pacific/Marquesas";
    }
    return timezone;
  }
}
