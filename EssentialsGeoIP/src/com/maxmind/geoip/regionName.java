package com.maxmind.geoip;
// generated automatically from admin/generate_regionName.pl
public class regionName {
static public String regionNameByCode(String country_code,String region_code) {
    String name = null;
    int region_code2 = -1;
    if (region_code == null) { return null; }
    if (region_code.equals("")) { return null; }

    if (    ((region_code.charAt(0) >= 48 ) && ( region_code.charAt(0) < ( 48 + 10 )))
         && ((region_code.charAt(1) >= 48 ) && ( region_code.charAt(1) < ( 48 + 10 )))
    ){
      // only numbers, that shorten the large switch statements
      region_code2 = (region_code.charAt(0)- 48) * 10 + region_code.charAt(1) - 48;
    }
	  else if (    (    ((region_code.charAt(0) >= 65) && (region_code.charAt(0) < (65 + 26)))
                 || ((region_code.charAt(0) >= 48) && (region_code.charAt(0) < (48 + 10))))
            && (    ((region_code.charAt(1) >= 65) && (region_code.charAt(1) < (65 + 26)))
                 || ((region_code.charAt(1) >= 48) && (region_code.charAt(1) < (48 + 10))))
  ) {

    region_code2 = (region_code.charAt(0) - 48) * (65 + 26 - 48) + region_code.charAt(1) - 48 + 100;
  }

  if (region_code2 == -1) {return null;}
    if (country_code.equals("CA") == true) {
      switch (region_code2) {
        case 849:
        name = "Alberta";
        break;
        case 893:
        name = "British Columbia";
        break;
        case 1365:
        name = "Manitoba";
        break;
        case 1408:
        name = "New Brunswick";
        break;
        case 1418:
        name = "Newfoundland";
        break;
        case 1425:
        name = "Nova Scotia";
        break;
        case 1427:
        name = "Nunavut";
        break;
        case 1463:
        name = "Ontario";
        break;
        case 1497:
        name = "Prince Edward Island";
        break;
        case 1538:
        name = "Quebec";
        break;
        case 1632:
        name = "Saskatchewan";
        break;
        case 1426:
        name = "Northwest Territories";
        break;
        case 1899:
        name = "Yukon Territory";
        break;
      }
    }
    if (country_code.equals("US") == true) {
      switch (region_code2) {
        case 848:
        name = "Armed Forces Americas";
        break;
        case 852:
        name = "Armed Forces Europe";
        break;
        case 858:
        name = "Alaska";
        break;
        case 859:
        name = "Alabama";
        break;
        case 863:
        name = "Armed Forces Pacific";
        break;
        case 865:
        name = "Arkansas";
        break;
        case 866:
        name = "American Samoa";
        break;
        case 873:
        name = "Arizona";
        break;
        case 934:
        name = "California";
        break;
        case 948:
        name = "Colorado";
        break;
        case 953:
        name = "Connecticut";
        break;
        case 979:
        name = "District of Columbia";
        break;
        case 981:
        name = "Delaware";
        break;
        case 1074:
        name = "Florida";
        break;
        case 1075:
        name = "Federated States of Micronesia";
        break;
        case 1106:
        name = "Georgia";
        break;
        case 1126:
        name = "Guam";
        break;
        case 1157:
        name = "Hawaii";
        break;
        case 1192:
        name = "Iowa";
        break;
        case 1195:
        name = "Idaho";
        break;
        case 1203:
        name = "Illinois";
        break;
        case 1205:
        name = "Indiana";
        break;
        case 1296:
        name = "Kansas";
        break;
        case 1302:
        name = "Kentucky";
        break;
        case 1321:
        name = "Louisiana";
        break;
        case 1364:
        name = "Massachusetts";
        break;
        case 1367:
        name = "Maryland";
        break;
        case 1368:
        name = "Maine";
        break;
        case 1371:
        name = "Marshall Islands";
        break;
        case 1372:
        name = "Michigan";
        break;
        case 1377:
        name = "Minnesota";
        break;
        case 1378:
        name = "Missouri";
        break;
        case 1379:
        name = "Northern Mariana Islands";
        break;
        case 1382:
        name = "Mississippi";
        break;
        case 1383:
        name = "Montana";
        break;
        case 1409:
        name = "North Carolina";
        break;
        case 1410:
        name = "North Dakota";
        break;
        case 1411:
        name = "Nebraska";
        break;
        case 1414:
        name = "New Hampshire";
        break;
        case 1416:
        name = "New Jersey";
        break;
        case 1419:
        name = "New Mexico";
        break;
        case 1428:
        name = "Nevada";
        break;
        case 1431:
        name = "New York";
        break;
        case 1457:
        name = "Ohio";
        break;
        case 1460:
        name = "Oklahoma";
        break;
        case 1467:
        name = "Oregon";
        break;
        case 1493:
        name = "Pennsylvania";
        break;
        case 1510:
        name = "Puerto Rico";
        break;
        case 1515:
        name = "Palau";
        break;
        case 1587:
        name = "Rhode Island";
        break;
        case 1624:
        name = "South Carolina";
        break;
        case 1625:
        name = "South Dakota";
        break;
        case 1678:
        name = "Tennessee";
        break;
        case 1688:
        name = "Texas";
        break;
        case 1727:
        name = "Utah";
        break;
        case 1751:
        name = "Virginia";
        break;
        case 1759:
        name = "Virgin Islands";
        break;
        case 1770:
        name = "Vermont";
        break;
        case 1794:
        name = "Washington";
        break;
        case 1815:
        name = "West Virginia";
        break;
        case 1802:
        name = "Wisconsin";
        break;
        case 1818:
        name = "Wyoming";
        break;
      }
    }
    if (country_code.equals("AD") == true) {
      switch (region_code2) {
        case 2:
        name = "Canillo";
        break;
        case 3:
        name = "Encamp";
        break;
        case 4:
        name = "La Massana";
        break;
        case 5:
        name = "Ordino";
        break;
        case 6:
        name = "Sant Julia de Loria";
        break;
        case 7:
        name = "Andorra la Vella";
        break;
        case 8:
        name = "Escaldes-Engordany";
        break;
      }
    }
    if (country_code.equals("AE") == true) {
      switch (region_code2) {
        case 1:
        name = "Abu Dhabi";
        break;
        case 2:
        name = "Ajman";
        break;
        case 3:
        name = "Dubai";
        break;
        case 4:
        name = "Fujairah";
        break;
        case 5:
        name = "Ras Al Khaimah";
        break;
        case 6:
        name = "Sharjah";
        break;
        case 7:
        name = "Umm Al Quwain";
        break;
      }
    }
    if (country_code.equals("AF") == true) {
      switch (region_code2) {
        case 1:
        name = "Badakhshan";
        break;
        case 2:
        name = "Badghis";
        break;
        case 3:
        name = "Baghlan";
        break;
        case 5:
        name = "Bamian";
        break;
        case 6:
        name = "Farah";
        break;
        case 7:
        name = "Faryab";
        break;
        case 8:
        name = "Ghazni";
        break;
        case 9:
        name = "Ghowr";
        break;
        case 10:
        name = "Helmand";
        break;
        case 11:
        name = "Herat";
        break;
        case 13:
        name = "Kabol";
        break;
        case 14:
        name = "Kapisa";
        break;
        case 15:
        name = "Konar";
        break;
        case 16:
        name = "Laghman";
        break;
        case 17:
        name = "Lowgar";
        break;
        case 18:
        name = "Nangarhar";
        break;
        case 19:
        name = "Nimruz";
        break;
        case 21:
        name = "Paktia";
        break;
        case 22:
        name = "Parvan";
        break;
        case 23:
        name = "Kandahar";
        break;
        case 24:
        name = "Kondoz";
        break;
        case 26:
        name = "Takhar";
        break;
        case 27:
        name = "Vardak";
        break;
        case 28:
        name = "Zabol";
        break;
        case 29:
        name = "Paktika";
        break;
        case 30:
        name = "Balkh";
        break;
        case 31:
        name = "Jowzjan";
        break;
        case 32:
        name = "Samangan";
        break;
        case 33:
        name = "Sar-e Pol";
        break;
        case 34:
        name = "Konar";
        break;
        case 35:
        name = "Laghman";
        break;
        case 36:
        name = "Paktia";
        break;
        case 37:
        name = "Khowst";
        break;
        case 38:
        name = "Nurestan";
        break;
        case 39:
        name = "Oruzgan";
        break;
        case 40:
        name = "Parvan";
        break;
        case 41:
        name = "Daykondi";
        break;
        case 42:
        name = "Panjshir";
        break;
      }
    }
    if (country_code.equals("AG") == true) {
      switch (region_code2) {
        case 1:
        name = "Barbuda";
        break;
        case 3:
        name = "Saint George";
        break;
        case 4:
        name = "Saint John";
        break;
        case 5:
        name = "Saint Mary";
        break;
        case 6:
        name = "Saint Paul";
        break;
        case 7:
        name = "Saint Peter";
        break;
        case 8:
        name = "Saint Philip";
        break;
      }
    }
    if (country_code.equals("AL") == true) {
      switch (region_code2) {
        case 40:
        name = "Berat";
        break;
        case 41:
        name = "Diber";
        break;
        case 42:
        name = "Durres";
        break;
        case 43:
        name = "Elbasan";
        break;
        case 44:
        name = "Fier";
        break;
        case 45:
        name = "Gjirokaster";
        break;
        case 46:
        name = "Korce";
        break;
        case 47:
        name = "Kukes";
        break;
        case 48:
        name = "Lezhe";
        break;
        case 49:
        name = "Shkoder";
        break;
        case 50:
        name = "Tirane";
        break;
        case 51:
        name = "Vlore";
        break;
      }
    }
    if (country_code.equals("AM") == true) {
      switch (region_code2) {
        case 1:
        name = "Aragatsotn";
        break;
        case 2:
        name = "Ararat";
        break;
        case 3:
        name = "Armavir";
        break;
        case 4:
        name = "Geghark'unik'";
        break;
        case 5:
        name = "Kotayk'";
        break;
        case 6:
        name = "Lorri";
        break;
        case 7:
        name = "Shirak";
        break;
        case 8:
        name = "Syunik'";
        break;
        case 9:
        name = "Tavush";
        break;
        case 10:
        name = "Vayots' Dzor";
        break;
        case 11:
        name = "Yerevan";
        break;
      }
    }
    if (country_code.equals("AO") == true) {
      switch (region_code2) {
        case 1:
        name = "Benguela";
        break;
        case 2:
        name = "Bie";
        break;
        case 3:
        name = "Cabinda";
        break;
        case 4:
        name = "Cuando Cubango";
        break;
        case 5:
        name = "Cuanza Norte";
        break;
        case 6:
        name = "Cuanza Sul";
        break;
        case 7:
        name = "Cunene";
        break;
        case 8:
        name = "Huambo";
        break;
        case 9:
        name = "Huila";
        break;
        case 10:
        name = "Luanda";
        break;
        case 12:
        name = "Malanje";
        break;
        case 13:
        name = "Namibe";
        break;
        case 14:
        name = "Moxico";
        break;
        case 15:
        name = "Uige";
        break;
        case 16:
        name = "Zaire";
        break;
        case 17:
        name = "Lunda Norte";
        break;
        case 18:
        name = "Lunda Sul";
        break;
        case 19:
        name = "Bengo";
        break;
        case 20:
        name = "Luanda";
        break;
      }
    }
    if (country_code.equals("AR") == true) {
      switch (region_code2) {
        case 1:
        name = "Buenos Aires";
        break;
        case 2:
        name = "Catamarca";
        break;
        case 3:
        name = "Chaco";
        break;
        case 4:
        name = "Chubut";
        break;
        case 5:
        name = "Cordoba";
        break;
        case 6:
        name = "Corrientes";
        break;
        case 7:
        name = "Distrito Federal";
        break;
        case 8:
        name = "Entre Rios";
        break;
        case 9:
        name = "Formosa";
        break;
        case 10:
        name = "Jujuy";
        break;
        case 11:
        name = "La Pampa";
        break;
        case 12:
        name = "La Rioja";
        break;
        case 13:
        name = "Mendoza";
        break;
        case 14:
        name = "Misiones";
        break;
        case 15:
        name = "Neuquen";
        break;
        case 16:
        name = "Rio Negro";
        break;
        case 17:
        name = "Salta";
        break;
        case 18:
        name = "San Juan";
        break;
        case 19:
        name = "San Luis";
        break;
        case 20:
        name = "Santa Cruz";
        break;
        case 21:
        name = "Santa Fe";
        break;
        case 22:
        name = "Santiago del Estero";
        break;
        case 23:
        name = "Tierra del Fuego";
        break;
        case 24:
        name = "Tucuman";
        break;
      }
    }
    if (country_code.equals("AT") == true) {
      switch (region_code2) {
        case 1:
        name = "Burgenland";
        break;
        case 2:
        name = "Karnten";
        break;
        case 3:
        name = "Niederosterreich";
        break;
        case 4:
        name = "Oberosterreich";
        break;
        case 5:
        name = "Salzburg";
        break;
        case 6:
        name = "Steiermark";
        break;
        case 7:
        name = "Tirol";
        break;
        case 8:
        name = "Vorarlberg";
        break;
        case 9:
        name = "Wien";
        break;
      }
    }
    if (country_code.equals("AU") == true) {
      switch (region_code2) {
        case 1:
        name = "Australian Capital Territory";
        break;
        case 2:
        name = "New South Wales";
        break;
        case 3:
        name = "Northern Territory";
        break;
        case 4:
        name = "Queensland";
        break;
        case 5:
        name = "South Australia";
        break;
        case 6:
        name = "Tasmania";
        break;
        case 7:
        name = "Victoria";
        break;
        case 8:
        name = "Western Australia";
        break;
      }
    }
    if (country_code.equals("AZ") == true) {
      switch (region_code2) {
        case 1:
        name = "Abseron";
        break;
        case 2:
        name = "Agcabadi";
        break;
        case 3:
        name = "Agdam";
        break;
        case 4:
        name = "Agdas";
        break;
        case 5:
        name = "Agstafa";
        break;
        case 6:
        name = "Agsu";
        break;
        case 7:
        name = "Ali Bayramli";
        break;
        case 8:
        name = "Astara";
        break;
        case 9:
        name = "Baki";
        break;
        case 10:
        name = "Balakan";
        break;
        case 11:
        name = "Barda";
        break;
        case 12:
        name = "Beylaqan";
        break;
        case 13:
        name = "Bilasuvar";
        break;
        case 14:
        name = "Cabrayil";
        break;
        case 15:
        name = "Calilabad";
        break;
        case 16:
        name = "Daskasan";
        break;
        case 17:
        name = "Davaci";
        break;
        case 18:
        name = "Fuzuli";
        break;
        case 19:
        name = "Gadabay";
        break;
        case 20:
        name = "Ganca";
        break;
        case 21:
        name = "Goranboy";
        break;
        case 22:
        name = "Goycay";
        break;
        case 23:
        name = "Haciqabul";
        break;
        case 24:
        name = "Imisli";
        break;
        case 25:
        name = "Ismayilli";
        break;
        case 26:
        name = "Kalbacar";
        break;
        case 27:
        name = "Kurdamir";
        break;
        case 28:
        name = "Lacin";
        break;
        case 29:
        name = "Lankaran";
        break;
        case 30:
        name = "Lankaran";
        break;
        case 31:
        name = "Lerik";
        break;
        case 32:
        name = "Masalli";
        break;
        case 33:
        name = "Mingacevir";
        break;
        case 34:
        name = "Naftalan";
        break;
        case 35:
        name = "Naxcivan";
        break;
        case 36:
        name = "Neftcala";
        break;
        case 37:
        name = "Oguz";
        break;
        case 38:
        name = "Qabala";
        break;
        case 39:
        name = "Qax";
        break;
        case 40:
        name = "Qazax";
        break;
        case 41:
        name = "Qobustan";
        break;
        case 42:
        name = "Quba";
        break;
        case 43:
        name = "Qubadli";
        break;
        case 44:
        name = "Qusar";
        break;
        case 45:
        name = "Saatli";
        break;
        case 46:
        name = "Sabirabad";
        break;
        case 47:
        name = "Saki";
        break;
        case 48:
        name = "Saki";
        break;
        case 49:
        name = "Salyan";
        break;
        case 50:
        name = "Samaxi";
        break;
        case 51:
        name = "Samkir";
        break;
        case 52:
        name = "Samux";
        break;
        case 53:
        name = "Siyazan";
        break;
        case 54:
        name = "Sumqayit";
        break;
        case 55:
        name = "Susa";
        break;
        case 56:
        name = "Susa";
        break;
        case 57:
        name = "Tartar";
        break;
        case 58:
        name = "Tovuz";
        break;
        case 59:
        name = "Ucar";
        break;
        case 60:
        name = "Xacmaz";
        break;
        case 61:
        name = "Xankandi";
        break;
        case 62:
        name = "Xanlar";
        break;
        case 63:
        name = "Xizi";
        break;
        case 64:
        name = "Xocali";
        break;
        case 65:
        name = "Xocavand";
        break;
        case 66:
        name = "Yardimli";
        break;
        case 67:
        name = "Yevlax";
        break;
        case 68:
        name = "Yevlax";
        break;
        case 69:
        name = "Zangilan";
        break;
        case 70:
        name = "Zaqatala";
        break;
        case 71:
        name = "Zardab";
        break;
      }
    }
    if (country_code.equals("BA") == true) {
      switch (region_code2) {
        case 1:
        name = "Federation of Bosnia and Herzegovina";
        break;
        case 2:
        name = "Republika Srpska";
        break;
      }
    }
    if (country_code.equals("BB") == true) {
      switch (region_code2) {
        case 1:
        name = "Christ Church";
        break;
        case 2:
        name = "Saint Andrew";
        break;
        case 3:
        name = "Saint George";
        break;
        case 4:
        name = "Saint James";
        break;
        case 5:
        name = "Saint John";
        break;
        case 6:
        name = "Saint Joseph";
        break;
        case 7:
        name = "Saint Lucy";
        break;
        case 8:
        name = "Saint Michael";
        break;
        case 9:
        name = "Saint Peter";
        break;
        case 10:
        name = "Saint Philip";
        break;
        case 11:
        name = "Saint Thomas";
        break;
      }
    }
    if (country_code.equals("BD") == true) {
      switch (region_code2) {
        case 1:
        name = "Barisal";
        break;
        case 4:
        name = "Bandarban";
        break;
        case 5:
        name = "Comilla";
        break;
        case 12:
        name = "Mymensingh";
        break;
        case 13:
        name = "Noakhali";
        break;
        case 15:
        name = "Patuakhali";
        break;
        case 22:
        name = "Bagerhat";
        break;
        case 23:
        name = "Bhola";
        break;
        case 24:
        name = "Bogra";
        break;
        case 25:
        name = "Barguna";
        break;
        case 26:
        name = "Brahmanbaria";
        break;
        case 27:
        name = "Chandpur";
        break;
        case 28:
        name = "Chapai Nawabganj";
        break;
        case 29:
        name = "Chattagram";
        break;
        case 30:
        name = "Chuadanga";
        break;
        case 31:
        name = "Cox's Bazar";
        break;
        case 32:
        name = "Dhaka";
        break;
        case 33:
        name = "Dinajpur";
        break;
        case 34:
        name = "Faridpur";
        break;
        case 35:
        name = "Feni";
        break;
        case 36:
        name = "Gaibandha";
        break;
        case 37:
        name = "Gazipur";
        break;
        case 38:
        name = "Gopalganj";
        break;
        case 39:
        name = "Habiganj";
        break;
        case 40:
        name = "Jaipurhat";
        break;
        case 41:
        name = "Jamalpur";
        break;
        case 42:
        name = "Jessore";
        break;
        case 43:
        name = "Jhalakati";
        break;
        case 44:
        name = "Jhenaidah";
        break;
        case 45:
        name = "Khagrachari";
        break;
        case 46:
        name = "Khulna";
        break;
        case 47:
        name = "Kishorganj";
        break;
        case 48:
        name = "Kurigram";
        break;
        case 49:
        name = "Kushtia";
        break;
        case 50:
        name = "Laksmipur";
        break;
        case 51:
        name = "Lalmonirhat";
        break;
        case 52:
        name = "Madaripur";
        break;
        case 53:
        name = "Magura";
        break;
        case 54:
        name = "Manikganj";
        break;
        case 55:
        name = "Meherpur";
        break;
        case 56:
        name = "Moulavibazar";
        break;
        case 57:
        name = "Munshiganj";
        break;
        case 58:
        name = "Naogaon";
        break;
        case 59:
        name = "Narail";
        break;
        case 60:
        name = "Narayanganj";
        break;
        case 61:
        name = "Narsingdi";
        break;
        case 62:
        name = "Nator";
        break;
        case 63:
        name = "Netrakona";
        break;
        case 64:
        name = "Nilphamari";
        break;
        case 65:
        name = "Pabna";
        break;
        case 66:
        name = "Panchagar";
        break;
        case 67:
        name = "Parbattya Chattagram";
        break;
        case 68:
        name = "Pirojpur";
        break;
        case 69:
        name = "Rajbari";
        break;
        case 70:
        name = "Rajshahi";
        break;
        case 71:
        name = "Rangpur";
        break;
        case 72:
        name = "Satkhira";
        break;
        case 73:
        name = "Shariyatpur";
        break;
        case 74:
        name = "Sherpur";
        break;
        case 75:
        name = "Sirajganj";
        break;
        case 76:
        name = "Sunamganj";
        break;
        case 77:
        name = "Sylhet";
        break;
        case 78:
        name = "Tangail";
        break;
        case 79:
        name = "Thakurgaon";
        break;
        case 81:
        name = "Dhaka";
        break;
        case 82:
        name = "Khulna";
        break;
        case 83:
        name = "Rajshahi";
        break;
        case 84:
        name = "Chittagong";
        break;
        case 85:
        name = "Barisal";
        break;
        case 86:
        name = "Sylhet";
        break;
      }
    }
    if (country_code.equals("BE") == true) {
      switch (region_code2) {
        case 1:
        name = "Antwerpen";
        break;
        case 2:
        name = "Brabant";
        break;
        case 3:
        name = "Hainaut";
        break;
        case 4:
        name = "Liege";
        break;
        case 5:
        name = "Limburg";
        break;
        case 6:
        name = "Luxembourg";
        break;
        case 7:
        name = "Namur";
        break;
        case 8:
        name = "Oost-Vlaanderen";
        break;
        case 9:
        name = "West-Vlaanderen";
        break;
        case 10:
        name = "Brabant Wallon";
        break;
        case 11:
        name = "Brussels Hoofdstedelijk Gewest";
        break;
        case 12:
        name = "Vlaams-Brabant";
        break;
      }
    }
    if (country_code.equals("BF") == true) {
      switch (region_code2) {
        case 15:
        name = "Bam";
        break;
        case 19:
        name = "Boulkiemde";
        break;
        case 20:
        name = "Ganzourgou";
        break;
        case 21:
        name = "Gnagna";
        break;
        case 28:
        name = "Kouritenga";
        break;
        case 33:
        name = "Oudalan";
        break;
        case 34:
        name = "Passore";
        break;
        case 36:
        name = "Sanguie";
        break;
        case 40:
        name = "Soum";
        break;
        case 42:
        name = "Tapoa";
        break;
        case 44:
        name = "Zoundweogo";
        break;
        case 45:
        name = "Bale";
        break;
        case 46:
        name = "Banwa";
        break;
        case 47:
        name = "Bazega";
        break;
        case 48:
        name = "Bougouriba";
        break;
        case 49:
        name = "Boulgou";
        break;
        case 50:
        name = "Gourma";
        break;
        case 51:
        name = "Houet";
        break;
        case 52:
        name = "Ioba";
        break;
        case 53:
        name = "Kadiogo";
        break;
        case 54:
        name = "Kenedougou";
        break;
        case 55:
        name = "Komoe";
        break;
        case 56:
        name = "Komondjari";
        break;
        case 57:
        name = "Kompienga";
        break;
        case 58:
        name = "Kossi";
        break;
        case 59:
        name = "Koulpelogo";
        break;
        case 60:
        name = "Kourweogo";
        break;
        case 61:
        name = "Leraba";
        break;
        case 62:
        name = "Loroum";
        break;
        case 63:
        name = "Mouhoun";
        break;
        case 64:
        name = "Namentenga";
        break;
        case 65:
        name = "Naouri";
        break;
        case 66:
        name = "Nayala";
        break;
        case 67:
        name = "Noumbiel";
        break;
        case 68:
        name = "Oubritenga";
        break;
        case 69:
        name = "Poni";
        break;
        case 70:
        name = "Sanmatenga";
        break;
        case 71:
        name = "Seno";
        break;
        case 72:
        name = "Sissili";
        break;
        case 73:
        name = "Sourou";
        break;
        case 74:
        name = "Tuy";
        break;
        case 75:
        name = "Yagha";
        break;
        case 76:
        name = "Yatenga";
        break;
        case 77:
        name = "Ziro";
        break;
        case 78:
        name = "Zondoma";
        break;
      }
    }
    if (country_code.equals("BG") == true) {
      switch (region_code2) {
        case 33:
        name = "Mikhaylovgrad";
        break;
        case 38:
        name = "Blagoevgrad";
        break;
        case 39:
        name = "Burgas";
        break;
        case 40:
        name = "Dobrich";
        break;
        case 41:
        name = "Gabrovo";
        break;
        case 42:
        name = "Grad Sofiya";
        break;
        case 43:
        name = "Khaskovo";
        break;
        case 44:
        name = "Kurdzhali";
        break;
        case 45:
        name = "Kyustendil";
        break;
        case 46:
        name = "Lovech";
        break;
        case 47:
        name = "Montana";
        break;
        case 48:
        name = "Pazardzhik";
        break;
        case 49:
        name = "Pernik";
        break;
        case 50:
        name = "Pleven";
        break;
        case 51:
        name = "Plovdiv";
        break;
        case 52:
        name = "Razgrad";
        break;
        case 53:
        name = "Ruse";
        break;
        case 54:
        name = "Shumen";
        break;
        case 55:
        name = "Silistra";
        break;
        case 56:
        name = "Sliven";
        break;
        case 57:
        name = "Smolyan";
        break;
        case 58:
        name = "Sofiya";
        break;
        case 59:
        name = "Stara Zagora";
        break;
        case 60:
        name = "Turgovishte";
        break;
        case 61:
        name = "Varna";
        break;
        case 62:
        name = "Veliko Turnovo";
        break;
        case 63:
        name = "Vidin";
        break;
        case 64:
        name = "Vratsa";
        break;
        case 65:
        name = "Yambol";
        break;
      }
    }
    if (country_code.equals("BH") == true) {
      switch (region_code2) {
        case 1:
        name = "Al Hadd";
        break;
        case 2:
        name = "Al Manamah";
        break;
        case 3:
        name = "Al Muharraq";
        break;
        case 5:
        name = "Jidd Hafs";
        break;
        case 6:
        name = "Sitrah";
        break;
        case 7:
        name = "Ar Rifa' wa al Mintaqah al Janubiyah";
        break;
        case 8:
        name = "Al Mintaqah al Gharbiyah";
        break;
        case 9:
        name = "Mintaqat Juzur Hawar";
        break;
        case 10:
        name = "Al Mintaqah ash Shamaliyah";
        break;
        case 11:
        name = "Al Mintaqah al Wusta";
        break;
        case 12:
        name = "Madinat";
        break;
        case 13:
        name = "Ar Rifa";
        break;
        case 14:
        name = "Madinat Hamad";
        break;
        case 15:
        name = "Al Muharraq";
        break;
        case 16:
        name = "Al Asimah";
        break;
        case 17:
        name = "Al Janubiyah";
        break;
        case 18:
        name = "Ash Shamaliyah";
        break;
        case 19:
        name = "Al Wusta";
        break;
      }
    }
    if (country_code.equals("BI") == true) {
      switch (region_code2) {
        case 2:
        name = "Bujumbura";
        break;
        case 9:
        name = "Bubanza";
        break;
        case 10:
        name = "Bururi";
        break;
        case 11:
        name = "Cankuzo";
        break;
        case 12:
        name = "Cibitoke";
        break;
        case 13:
        name = "Gitega";
        break;
        case 14:
        name = "Karuzi";
        break;
        case 15:
        name = "Kayanza";
        break;
        case 16:
        name = "Kirundo";
        break;
        case 17:
        name = "Makamba";
        break;
        case 18:
        name = "Muyinga";
        break;
        case 19:
        name = "Ngozi";
        break;
        case 20:
        name = "Rutana";
        break;
        case 21:
        name = "Ruyigi";
        break;
        case 22:
        name = "Muramvya";
        break;
        case 23:
        name = "Mwaro";
        break;
      }
    }
    if (country_code.equals("BJ") == true) {
      switch (region_code2) {
        case 1:
        name = "Atakora";
        break;
        case 2:
        name = "Atlantique";
        break;
        case 3:
        name = "Borgou";
        break;
        case 4:
        name = "Mono";
        break;
        case 5:
        name = "Oueme";
        break;
        case 6:
        name = "Zou";
        break;
        case 7:
        name = "Alibori";
        break;
        case 8:
        name = "Atakora";
        break;
        case 9:
        name = "Atlanyique";
        break;
        case 10:
        name = "Borgou";
        break;
        case 11:
        name = "Collines";
        break;
        case 12:
        name = "Kouffo";
        break;
        case 13:
        name = "Donga";
        break;
        case 14:
        name = "Littoral";
        break;
        case 15:
        name = "Mono";
        break;
        case 16:
        name = "Oueme";
        break;
        case 17:
        name = "Plateau";
        break;
        case 18:
        name = "Zou";
        break;
      }
    }
    if (country_code.equals("BM") == true) {
      switch (region_code2) {
        case 1:
        name = "Devonshire";
        break;
        case 2:
        name = "Hamilton";
        break;
        case 3:
        name = "Hamilton";
        break;
        case 4:
        name = "Paget";
        break;
        case 5:
        name = "Pembroke";
        break;
        case 6:
        name = "Saint George";
        break;
        case 7:
        name = "Saint George's";
        break;
        case 8:
        name = "Sandys";
        break;
        case 9:
        name = "Smiths";
        break;
        case 10:
        name = "Southampton";
        break;
        case 11:
        name = "Warwick";
        break;
      }
    }
    if (country_code.equals("BN") == true) {
      switch (region_code2) {
        case 7:
        name = "Alibori";
        break;
        case 8:
        name = "Belait";
        break;
        case 9:
        name = "Brunei and Muara";
        break;
        case 10:
        name = "Temburong";
        break;
        case 11:
        name = "Collines";
        break;
        case 12:
        name = "Kouffo";
        break;
        case 13:
        name = "Donga";
        break;
        case 14:
        name = "Littoral";
        break;
        case 15:
        name = "Tutong";
        break;
        case 16:
        name = "Oueme";
        break;
        case 17:
        name = "Plateau";
        break;
        case 18:
        name = "Zou";
        break;
      }
    }
    if (country_code.equals("BO") == true) {
      switch (region_code2) {
        case 1:
        name = "Chuquisaca";
        break;
        case 2:
        name = "Cochabamba";
        break;
        case 3:
        name = "El Beni";
        break;
        case 4:
        name = "La Paz";
        break;
        case 5:
        name = "Oruro";
        break;
        case 6:
        name = "Pando";
        break;
        case 7:
        name = "Potosi";
        break;
        case 8:
        name = "Santa Cruz";
        break;
        case 9:
        name = "Tarija";
        break;
      }
    }
    if (country_code.equals("BR") == true) {
      switch (region_code2) {
        case 1:
        name = "Acre";
        break;
        case 2:
        name = "Alagoas";
        break;
        case 3:
        name = "Amapa";
        break;
        case 4:
        name = "Amazonas";
        break;
        case 5:
        name = "Bahia";
        break;
        case 6:
        name = "Ceara";
        break;
        case 7:
        name = "Distrito Federal";
        break;
        case 8:
        name = "Espirito Santo";
        break;
        case 11:
        name = "Mato Grosso do Sul";
        break;
        case 13:
        name = "Maranhao";
        break;
        case 14:
        name = "Mato Grosso";
        break;
        case 15:
        name = "Minas Gerais";
        break;
        case 16:
        name = "Para";
        break;
        case 17:
        name = "Paraiba";
        break;
        case 18:
        name = "Parana";
        break;
        case 20:
        name = "Piaui";
        break;
        case 21:
        name = "Rio de Janeiro";
        break;
        case 22:
        name = "Rio Grande do Norte";
        break;
        case 23:
        name = "Rio Grande do Sul";
        break;
        case 24:
        name = "Rondonia";
        break;
        case 25:
        name = "Roraima";
        break;
        case 26:
        name = "Santa Catarina";
        break;
        case 27:
        name = "Sao Paulo";
        break;
        case 28:
        name = "Sergipe";
        break;
        case 29:
        name = "Goias";
        break;
        case 30:
        name = "Pernambuco";
        break;
        case 31:
        name = "Tocantins";
        break;
      }
    }
    if (country_code.equals("BS") == true) {
      switch (region_code2) {
        case 5:
        name = "Bimini";
        break;
        case 6:
        name = "Cat Island";
        break;
        case 10:
        name = "Exuma";
        break;
        case 13:
        name = "Inagua";
        break;
        case 15:
        name = "Long Island";
        break;
        case 16:
        name = "Mayaguana";
        break;
        case 18:
        name = "Ragged Island";
        break;
        case 22:
        name = "Harbour Island";
        break;
        case 23:
        name = "New Providence";
        break;
        case 24:
        name = "Acklins and Crooked Islands";
        break;
        case 25:
        name = "Freeport";
        break;
        case 26:
        name = "Fresh Creek";
        break;
        case 27:
        name = "Governor's Harbour";
        break;
        case 28:
        name = "Green Turtle Cay";
        break;
        case 29:
        name = "High Rock";
        break;
        case 30:
        name = "Kemps Bay";
        break;
        case 31:
        name = "Marsh Harbour";
        break;
        case 32:
        name = "Nichollstown and Berry Islands";
        break;
        case 33:
        name = "Rock Sound";
        break;
        case 34:
        name = "Sandy Point";
        break;
        case 35:
        name = "San Salvador and Rum Cay";
        break;
      }
    }
    if (country_code.equals("BT") == true) {
      switch (region_code2) {
        case 5:
        name = "Bumthang";
        break;
        case 6:
        name = "Chhukha";
        break;
        case 7:
        name = "Chirang";
        break;
        case 8:
        name = "Daga";
        break;
        case 9:
        name = "Geylegphug";
        break;
        case 10:
        name = "Ha";
        break;
        case 11:
        name = "Lhuntshi";
        break;
        case 12:
        name = "Mongar";
        break;
        case 13:
        name = "Paro";
        break;
        case 14:
        name = "Pemagatsel";
        break;
        case 15:
        name = "Punakha";
        break;
        case 16:
        name = "Samchi";
        break;
        case 17:
        name = "Samdrup";
        break;
        case 18:
        name = "Shemgang";
        break;
        case 19:
        name = "Tashigang";
        break;
        case 20:
        name = "Thimphu";
        break;
        case 21:
        name = "Tongsa";
        break;
        case 22:
        name = "Wangdi Phodrang";
        break;
      }
    }
    if (country_code.equals("BW") == true) {
      switch (region_code2) {
        case 1:
        name = "Central";
        break;
        case 3:
        name = "Ghanzi";
        break;
        case 4:
        name = "Kgalagadi";
        break;
        case 5:
        name = "Kgatleng";
        break;
        case 6:
        name = "Kweneng";
        break;
        case 8:
        name = "North-East";
        break;
        case 9:
        name = "South-East";
        break;
        case 10:
        name = "Southern";
        break;
        case 11:
        name = "North-West";
        break;
      }
    }
    if (country_code.equals("BY") == true) {
      switch (region_code2) {
        case 1:
        name = "Brestskaya Voblasts'";
        break;
        case 2:
        name = "Homyel'skaya Voblasts'";
        break;
        case 3:
        name = "Hrodzyenskaya Voblasts'";
        break;
        case 4:
        name = "Minsk";
        break;
        case 5:
        name = "Minskaya Voblasts'";
        break;
        case 6:
        name = "Mahilyowskaya Voblasts'";
        break;
        case 7:
        name = "Vitsyebskaya Voblasts'";
        break;
      }
    }
    if (country_code.equals("BZ") == true) {
      switch (region_code2) {
        case 1:
        name = "Belize";
        break;
        case 2:
        name = "Cayo";
        break;
        case 3:
        name = "Corozal";
        break;
        case 4:
        name = "Orange Walk";
        break;
        case 5:
        name = "Stann Creek";
        break;
        case 6:
        name = "Toledo";
        break;
      }
    }
    if (country_code.equals("CD") == true) {
      switch (region_code2) {
        case 1:
        name = "Bandundu";
        break;
        case 2:
        name = "Equateur";
        break;
        case 4:
        name = "Kasai-Oriental";
        break;
        case 5:
        name = "Katanga";
        break;
        case 6:
        name = "Kinshasa";
        break;
        case 7:
        name = "Kivu";
        break;
        case 8:
        name = "Bas-Congo";
        break;
        case 9:
        name = "Orientale";
        break;
        case 10:
        name = "Maniema";
        break;
        case 11:
        name = "Nord-Kivu";
        break;
        case 12:
        name = "Sud-Kivu";
        break;
        case 13:
        name = "Cuvette";
        break;
      }
    }
    if (country_code.equals("CF") == true) {
      switch (region_code2) {
        case 1:
        name = "Bamingui-Bangoran";
        break;
        case 2:
        name = "Basse-Kotto";
        break;
        case 3:
        name = "Haute-Kotto";
        break;
        case 4:
        name = "Mambere-Kadei";
        break;
        case 5:
        name = "Haut-Mbomou";
        break;
        case 6:
        name = "Kemo";
        break;
        case 7:
        name = "Lobaye";
        break;
        case 8:
        name = "Mbomou";
        break;
        case 9:
        name = "Nana-Mambere";
        break;
        case 11:
        name = "Ouaka";
        break;
        case 12:
        name = "Ouham";
        break;
        case 13:
        name = "Ouham-Pende";
        break;
        case 14:
        name = "Cuvette-Ouest";
        break;
        case 15:
        name = "Nana-Grebizi";
        break;
        case 16:
        name = "Sangha-Mbaere";
        break;
        case 17:
        name = "Ombella-Mpoko";
        break;
        case 18:
        name = "Bangui";
        break;
      }
    }
    if (country_code.equals("CG") == true) {
      switch (region_code2) {
        case 1:
        name = "Bouenza";
        break;
        case 3:
        name = "Cuvette";
        break;
        case 4:
        name = "Kouilou";
        break;
        case 5:
        name = "Lekoumou";
        break;
        case 6:
        name = "Likouala";
        break;
        case 7:
        name = "Niari";
        break;
        case 8:
        name = "Plateaux";
        break;
        case 10:
        name = "Sangha";
        break;
        case 11:
        name = "Pool";
        break;
        case 12:
        name = "Brazzaville";
        break;
      }
    }
    if (country_code.equals("CH") == true) {
      switch (region_code2) {
        case 1:
        name = "Aargau";
        break;
        case 2:
        name = "Ausser-Rhoden";
        break;
        case 3:
        name = "Basel-Landschaft";
        break;
        case 4:
        name = "Basel-Stadt";
        break;
        case 5:
        name = "Bern";
        break;
        case 6:
        name = "Fribourg";
        break;
        case 7:
        name = "Geneve";
        break;
        case 8:
        name = "Glarus";
        break;
        case 9:
        name = "Graubunden";
        break;
        case 10:
        name = "Inner-Rhoden";
        break;
        case 11:
        name = "Luzern";
        break;
        case 12:
        name = "Neuchatel";
        break;
        case 13:
        name = "Nidwalden";
        break;
        case 14:
        name = "Obwalden";
        break;
        case 15:
        name = "Sankt Gallen";
        break;
        case 16:
        name = "Schaffhausen";
        break;
        case 17:
        name = "Schwyz";
        break;
        case 18:
        name = "Solothurn";
        break;
        case 19:
        name = "Thurgau";
        break;
        case 20:
        name = "Ticino";
        break;
        case 21:
        name = "Uri";
        break;
        case 22:
        name = "Valais";
        break;
        case 23:
        name = "Vaud";
        break;
        case 24:
        name = "Zug";
        break;
        case 25:
        name = "Zurich";
        break;
        case 26:
        name = "Jura";
        break;
      }
    }
    if (country_code.equals("CI") == true) {
      switch (region_code2) {
        case 5:
        name = "Atacama";
        break;
        case 6:
        name = "Biobio";
        break;
        case 51:
        name = "Sassandra";
        break;
        case 61:
        name = "Abidjan";
        break;
        case 74:
        name = "Agneby";
        break;
        case 75:
        name = "Bafing";
        break;
        case 76:
        name = "Bas-Sassandra";
        break;
        case 77:
        name = "Denguele";
        break;
        case 78:
        name = "Dix-Huit Montagnes";
        break;
        case 79:
        name = "Fromager";
        break;
        case 80:
        name = "Haut-Sassandra";
        break;
        case 81:
        name = "Lacs";
        break;
        case 82:
        name = "Lagunes";
        break;
        case 83:
        name = "Marahoue";
        break;
        case 84:
        name = "Moyen-Cavally";
        break;
        case 85:
        name = "Moyen-Comoe";
        break;
        case 86:
        name = "N'zi-Comoe";
        break;
        case 87:
        name = "Savanes";
        break;
        case 88:
        name = "Sud-Bandama";
        break;
        case 89:
        name = "Sud-Comoe";
        break;
        case 90:
        name = "Vallee du Bandama";
        break;
        case 91:
        name = "Worodougou";
        break;
        case 92:
        name = "Zanzan";
        break;
      }
    }
    if (country_code.equals("CL") == true) {
      switch (region_code2) {
        case 1:
        name = "Valparaiso";
        break;
        case 2:
        name = "Aisen del General Carlos Ibanez del Campo";
        break;
        case 3:
        name = "Antofagasta";
        break;
        case 4:
        name = "Araucania";
        break;
        case 5:
        name = "Atacama";
        break;
        case 6:
        name = "Bio-Bio";
        break;
        case 7:
        name = "Coquimbo";
        break;
        case 8:
        name = "Libertador General Bernardo O'Higgins";
        break;
        case 9:
        name = "Los Lagos";
        break;
        case 10:
        name = "Magallanes y de la Antartica Chilena";
        break;
        case 11:
        name = "Maule";
        break;
        case 12:
        name = "Region Metropolitana";
        break;
        case 13:
        name = "Tarapaca";
        break;
        case 14:
        name = "Los Lagos";
        break;
        case 15:
        name = "Tarapaca";
        break;
        case 16:
        name = "Arica y Parinacota";
        break;
        case 17:
        name = "Los Rios";
        break;
      }
    }
    if (country_code.equals("CM") == true) {
      switch (region_code2) {
        case 4:
        name = "Est";
        break;
        case 5:
        name = "Littoral";
        break;
        case 7:
        name = "Nord-Ouest";
        break;
        case 8:
        name = "Ouest";
        break;
        case 9:
        name = "Sud-Ouest";
        break;
        case 10:
        name = "Adamaoua";
        break;
        case 11:
        name = "Centre";
        break;
        case 12:
        name = "Extreme-Nord";
        break;
        case 13:
        name = "Nord";
        break;
        case 14:
        name = "Sud";
        break;
      }
    }
    if (country_code.equals("CN") == true) {
      switch (region_code2) {
        case 1:
        name = "Anhui";
        break;
        case 2:
        name = "Zhejiang";
        break;
        case 3:
        name = "Jiangxi";
        break;
        case 4:
        name = "Jiangsu";
        break;
        case 5:
        name = "Jilin";
        break;
        case 6:
        name = "Qinghai";
        break;
        case 7:
        name = "Fujian";
        break;
        case 8:
        name = "Heilongjiang";
        break;
        case 9:
        name = "Henan";
        break;
        case 10:
        name = "Hebei";
        break;
        case 11:
        name = "Hunan";
        break;
        case 12:
        name = "Hubei";
        break;
        case 13:
        name = "Xinjiang";
        break;
        case 14:
        name = "Xizang";
        break;
        case 15:
        name = "Gansu";
        break;
        case 16:
        name = "Guangxi";
        break;
        case 18:
        name = "Guizhou";
        break;
        case 19:
        name = "Liaoning";
        break;
        case 20:
        name = "Nei Mongol";
        break;
        case 21:
        name = "Ningxia";
        break;
        case 22:
        name = "Beijing";
        break;
        case 23:
        name = "Shanghai";
        break;
        case 24:
        name = "Shanxi";
        break;
        case 25:
        name = "Shandong";
        break;
        case 26:
        name = "Shaanxi";
        break;
        case 28:
        name = "Tianjin";
        break;
        case 29:
        name = "Yunnan";
        break;
        case 30:
        name = "Guangdong";
        break;
        case 31:
        name = "Hainan";
        break;
        case 32:
        name = "Sichuan";
        break;
        case 33:
        name = "Chongqing";
        break;
      }
    }
    if (country_code.equals("CO") == true) {
      switch (region_code2) {
        case 1:
        name = "Amazonas";
        break;
        case 2:
        name = "Antioquia";
        break;
        case 3:
        name = "Arauca";
        break;
        case 4:
        name = "Atlantico";
        break;
        case 5:
        name = "Bolivar Department";
        break;
        case 6:
        name = "Boyaca Department";
        break;
        case 7:
        name = "Caldas Department";
        break;
        case 8:
        name = "Caqueta";
        break;
        case 9:
        name = "Cauca";
        break;
        case 10:
        name = "Cesar";
        break;
        case 11:
        name = "Choco";
        break;
        case 12:
        name = "Cordoba";
        break;
        case 14:
        name = "Guaviare";
        break;
        case 15:
        name = "Guainia";
        break;
        case 16:
        name = "Huila";
        break;
        case 17:
        name = "La Guajira";
        break;
        case 18:
        name = "Magdalena Department";
        break;
        case 19:
        name = "Meta";
        break;
        case 20:
        name = "Narino";
        break;
        case 21:
        name = "Norte de Santander";
        break;
        case 22:
        name = "Putumayo";
        break;
        case 23:
        name = "Quindio";
        break;
        case 24:
        name = "Risaralda";
        break;
        case 25:
        name = "San Andres y Providencia";
        break;
        case 26:
        name = "Santander";
        break;
        case 27:
        name = "Sucre";
        break;
        case 28:
        name = "Tolima";
        break;
        case 29:
        name = "Valle del Cauca";
        break;
        case 30:
        name = "Vaupes";
        break;
        case 31:
        name = "Vichada";
        break;
        case 32:
        name = "Casanare";
        break;
        case 33:
        name = "Cundinamarca";
        break;
        case 34:
        name = "Distrito Especial";
        break;
        case 35:
        name = "Bolivar";
        break;
        case 36:
        name = "Boyaca";
        break;
        case 37:
        name = "Caldas";
        break;
        case 38:
        name = "Magdalena";
        break;
      }
    }
    if (country_code.equals("CR") == true) {
      switch (region_code2) {
        case 1:
        name = "Alajuela";
        break;
        case 2:
        name = "Cartago";
        break;
        case 3:
        name = "Guanacaste";
        break;
        case 4:
        name = "Heredia";
        break;
        case 6:
        name = "Limon";
        break;
        case 7:
        name = "Puntarenas";
        break;
        case 8:
        name = "San Jose";
        break;
      }
    }
    if (country_code.equals("CU") == true) {
      switch (region_code2) {
        case 1:
        name = "Pinar del Rio";
        break;
        case 2:
        name = "Ciudad de la Habana";
        break;
        case 3:
        name = "Matanzas";
        break;
        case 4:
        name = "Isla de la Juventud";
        break;
        case 5:
        name = "Camaguey";
        break;
        case 7:
        name = "Ciego de Avila";
        break;
        case 8:
        name = "Cienfuegos";
        break;
        case 9:
        name = "Granma";
        break;
        case 10:
        name = "Guantanamo";
        break;
        case 11:
        name = "La Habana";
        break;
        case 12:
        name = "Holguin";
        break;
        case 13:
        name = "Las Tunas";
        break;
        case 14:
        name = "Sancti Spiritus";
        break;
        case 15:
        name = "Santiago de Cuba";
        break;
        case 16:
        name = "Villa Clara";
        break;
      }
    }
    if (country_code.equals("CV") == true) {
      switch (region_code2) {
        case 1:
        name = "Boa Vista";
        break;
        case 2:
        name = "Brava";
        break;
        case 4:
        name = "Maio";
        break;
        case 5:
        name = "Paul";
        break;
        case 7:
        name = "Ribeira Grande";
        break;
        case 8:
        name = "Sal";
        break;
        case 10:
        name = "Sao Nicolau";
        break;
        case 11:
        name = "Sao Vicente";
        break;
        case 13:
        name = "Mosteiros";
        break;
        case 14:
        name = "Praia";
        break;
        case 15:
        name = "Santa Catarina";
        break;
        case 16:
        name = "Santa Cruz";
        break;
        case 17:
        name = "Sao Domingos";
        break;
        case 18:
        name = "Sao Filipe";
        break;
        case 19:
        name = "Sao Miguel";
        break;
        case 20:
        name = "Tarrafal";
        break;
      }
    }
    if (country_code.equals("CY") == true) {
      switch (region_code2) {
        case 1:
        name = "Famagusta";
        break;
        case 2:
        name = "Kyrenia";
        break;
        case 3:
        name = "Larnaca";
        break;
        case 4:
        name = "Nicosia";
        break;
        case 5:
        name = "Limassol";
        break;
        case 6:
        name = "Paphos";
        break;
      }
    }
    if (country_code.equals("CZ") == true) {
      switch (region_code2) {
        case 3:
        name = "Blansko";
        break;
        case 4:
        name = "Breclav";
        break;
        case 20:
        name = "Hradec Kralove";
        break;
        case 21:
        name = "Jablonec nad Nisou";
        break;
        case 23:
        name = "Jicin";
        break;
        case 24:
        name = "Jihlava";
        break;
        case 30:
        name = "Kolin";
        break;
        case 33:
        name = "Liberec";
        break;
        case 36:
        name = "Melnik";
        break;
        case 37:
        name = "Mlada Boleslav";
        break;
        case 39:
        name = "Nachod";
        break;
        case 41:
        name = "Nymburk";
        break;
        case 45:
        name = "Pardubice";
        break;
        case 52:
        name = "Hlavni mesto Praha";
        break;
        case 61:
        name = "Semily";
        break;
        case 70:
        name = "Trutnov";
        break;
        case 78:
        name = "Jihomoravsky kraj";
        break;
        case 79:
        name = "Jihocesky kraj";
        break;
        case 80:
        name = "Vysocina";
        break;
        case 81:
        name = "Karlovarsky kraj";
        break;
        case 82:
        name = "Kralovehradecky kraj";
        break;
        case 83:
        name = "Liberecky kraj";
        break;
        case 84:
        name = "Olomoucky kraj";
        break;
        case 85:
        name = "Moravskoslezsky kraj";
        break;
        case 86:
        name = "Pardubicky kraj";
        break;
        case 87:
        name = "Plzensky kraj";
        break;
        case 88:
        name = "Stredocesky kraj";
        break;
        case 89:
        name = "Ustecky kraj";
        break;
        case 90:
        name = "Zlinsky kraj";
        break;
      }
    }
    if (country_code.equals("DE") == true) {
      switch (region_code2) {
        case 1:
        name = "Baden-Wurttemberg";
        break;
        case 2:
        name = "Bayern";
        break;
        case 3:
        name = "Bremen";
        break;
        case 4:
        name = "Hamburg";
        break;
        case 5:
        name = "Hessen";
        break;
        case 6:
        name = "Niedersachsen";
        break;
        case 7:
        name = "Nordrhein-Westfalen";
        break;
        case 8:
        name = "Rheinland-Pfalz";
        break;
        case 9:
        name = "Saarland";
        break;
        case 10:
        name = "Schleswig-Holstein";
        break;
        case 11:
        name = "Brandenburg";
        break;
        case 12:
        name = "Mecklenburg-Vorpommern";
        break;
        case 13:
        name = "Sachsen";
        break;
        case 14:
        name = "Sachsen-Anhalt";
        break;
        case 15:
        name = "Thuringen";
        break;
        case 16:
        name = "Berlin";
        break;
      }
    }
    if (country_code.equals("DJ") == true) {
      switch (region_code2) {
        case 1:
        name = "Ali Sabieh";
        break;
        case 4:
        name = "Obock";
        break;
        case 5:
        name = "Tadjoura";
        break;
        case 6:
        name = "Dikhil";
        break;
        case 7:
        name = "Djibouti";
        break;
        case 8:
        name = "Arta";
        break;
      }
    }
    if (country_code.equals("DK") == true) {
      switch (region_code2) {
        case 17:
        name = "Hovedstaden";
        break;
        case 18:
        name = "Midtjylland";
        break;
        case 19:
        name = "Nordjylland";
        break;
        case 20:
        name = "Sjelland";
        break;
        case 21:
        name = "Syddanmark";
        break;
      }
    }
    if (country_code.equals("DM") == true) {
      switch (region_code2) {
        case 2:
        name = "Saint Andrew";
        break;
        case 3:
        name = "Saint David";
        break;
        case 4:
        name = "Saint George";
        break;
        case 5:
        name = "Saint John";
        break;
        case 6:
        name = "Saint Joseph";
        break;
        case 7:
        name = "Saint Luke";
        break;
        case 8:
        name = "Saint Mark";
        break;
        case 9:
        name = "Saint Patrick";
        break;
        case 10:
        name = "Saint Paul";
        break;
        case 11:
        name = "Saint Peter";
        break;
      }
    }
    if (country_code.equals("DO") == true) {
      switch (region_code2) {
        case 1:
        name = "Azua";
        break;
        case 2:
        name = "Baoruco";
        break;
        case 3:
        name = "Barahona";
        break;
        case 4:
        name = "Dajabon";
        break;
        case 5:
        name = "Distrito Nacional";
        break;
        case 6:
        name = "Duarte";
        break;
        case 8:
        name = "Espaillat";
        break;
        case 9:
        name = "Independencia";
        break;
        case 10:
        name = "La Altagracia";
        break;
        case 11:
        name = "Elias Pina";
        break;
        case 12:
        name = "La Romana";
        break;
        case 14:
        name = "Maria Trinidad Sanchez";
        break;
        case 15:
        name = "Monte Cristi";
        break;
        case 16:
        name = "Pedernales";
        break;
        case 17:
        name = "Peravia";
        break;
        case 18:
        name = "Puerto Plata";
        break;
        case 19:
        name = "Salcedo";
        break;
        case 20:
        name = "Samana";
        break;
        case 21:
        name = "Sanchez Ramirez";
        break;
        case 23:
        name = "San Juan";
        break;
        case 24:
        name = "San Pedro De Macoris";
        break;
        case 25:
        name = "Santiago";
        break;
        case 26:
        name = "Santiago Rodriguez";
        break;
        case 27:
        name = "Valverde";
        break;
        case 28:
        name = "El Seibo";
        break;
        case 29:
        name = "Hato Mayor";
        break;
        case 30:
        name = "La Vega";
        break;
        case 31:
        name = "Monsenor Nouel";
        break;
        case 32:
        name = "Monte Plata";
        break;
        case 33:
        name = "San Cristobal";
        break;
        case 34:
        name = "Distrito Nacional";
        break;
        case 35:
        name = "Peravia";
        break;
        case 36:
        name = "San Jose de Ocoa";
        break;
        case 37:
        name = "Santo Domingo";
        break;
      }
    }
    if (country_code.equals("DZ") == true) {
      switch (region_code2) {
        case 1:
        name = "Alger";
        break;
        case 3:
        name = "Batna";
        break;
        case 4:
        name = "Constantine";
        break;
        case 6:
        name = "Medea";
        break;
        case 7:
        name = "Mostaganem";
        break;
        case 9:
        name = "Oran";
        break;
        case 10:
        name = "Saida";
        break;
        case 12:
        name = "Setif";
        break;
        case 13:
        name = "Tiaret";
        break;
        case 14:
        name = "Tizi Ouzou";
        break;
        case 15:
        name = "Tlemcen";
        break;
        case 18:
        name = "Bejaia";
        break;
        case 19:
        name = "Biskra";
        break;
        case 20:
        name = "Blida";
        break;
        case 21:
        name = "Bouira";
        break;
        case 22:
        name = "Djelfa";
        break;
        case 23:
        name = "Guelma";
        break;
        case 24:
        name = "Jijel";
        break;
        case 25:
        name = "Laghouat";
        break;
        case 26:
        name = "Mascara";
        break;
        case 27:
        name = "M'sila";
        break;
        case 29:
        name = "Oum el Bouaghi";
        break;
        case 30:
        name = "Sidi Bel Abbes";
        break;
        case 31:
        name = "Skikda";
        break;
        case 33:
        name = "Tebessa";
        break;
        case 34:
        name = "Adrar";
        break;
        case 35:
        name = "Ain Defla";
        break;
        case 36:
        name = "Ain Temouchent";
        break;
        case 37:
        name = "Annaba";
        break;
        case 38:
        name = "Bechar";
        break;
        case 39:
        name = "Bordj Bou Arreridj";
        break;
        case 40:
        name = "Boumerdes";
        break;
        case 41:
        name = "Chlef";
        break;
        case 42:
        name = "El Bayadh";
        break;
        case 43:
        name = "El Oued";
        break;
        case 44:
        name = "El Tarf";
        break;
        case 45:
        name = "Ghardaia";
        break;
        case 46:
        name = "Illizi";
        break;
        case 47:
        name = "Khenchela";
        break;
        case 48:
        name = "Mila";
        break;
        case 49:
        name = "Naama";
        break;
        case 50:
        name = "Ouargla";
        break;
        case 51:
        name = "Relizane";
        break;
        case 52:
        name = "Souk Ahras";
        break;
        case 53:
        name = "Tamanghasset";
        break;
        case 54:
        name = "Tindouf";
        break;
        case 55:
        name = "Tipaza";
        break;
        case 56:
        name = "Tissemsilt";
        break;
      }
    }
    if (country_code.equals("EC") == true) {
      switch (region_code2) {
        case 1:
        name = "Galapagos";
        break;
        case 2:
        name = "Azuay";
        break;
        case 3:
        name = "Bolivar";
        break;
        case 4:
        name = "Canar";
        break;
        case 5:
        name = "Carchi";
        break;
        case 6:
        name = "Chimborazo";
        break;
        case 7:
        name = "Cotopaxi";
        break;
        case 8:
        name = "El Oro";
        break;
        case 9:
        name = "Esmeraldas";
        break;
        case 10:
        name = "Guayas";
        break;
        case 11:
        name = "Imbabura";
        break;
        case 12:
        name = "Loja";
        break;
        case 13:
        name = "Los Rios";
        break;
        case 14:
        name = "Manabi";
        break;
        case 15:
        name = "Morona-Santiago";
        break;
        case 17:
        name = "Pastaza";
        break;
        case 18:
        name = "Pichincha";
        break;
        case 19:
        name = "Tungurahua";
        break;
        case 20:
        name = "Zamora-Chinchipe";
        break;
        case 22:
        name = "Sucumbios";
        break;
        case 23:
        name = "Napo";
        break;
        case 24:
        name = "Orellana";
        break;
      }
    }
    if (country_code.equals("EE") == true) {
      switch (region_code2) {
        case 1:
        name = "Harjumaa";
        break;
        case 2:
        name = "Hiiumaa";
        break;
        case 3:
        name = "Ida-Virumaa";
        break;
        case 4:
        name = "Jarvamaa";
        break;
        case 5:
        name = "Jogevamaa";
        break;
        case 6:
        name = "Kohtla-Jarve";
        break;
        case 7:
        name = "Laanemaa";
        break;
        case 8:
        name = "Laane-Virumaa";
        break;
        case 9:
        name = "Narva";
        break;
        case 10:
        name = "Parnu";
        break;
        case 11:
        name = "Parnumaa";
        break;
        case 12:
        name = "Polvamaa";
        break;
        case 13:
        name = "Raplamaa";
        break;
        case 14:
        name = "Saaremaa";
        break;
        case 15:
        name = "Sillamae";
        break;
        case 16:
        name = "Tallinn";
        break;
        case 17:
        name = "Tartu";
        break;
        case 18:
        name = "Tartumaa";
        break;
        case 19:
        name = "Valgamaa";
        break;
        case 20:
        name = "Viljandimaa";
        break;
        case 21:
        name = "Vorumaa";
        break;
      }
    }
    if (country_code.equals("EG") == true) {
      switch (region_code2) {
        case 1:
        name = "Ad Daqahliyah";
        break;
        case 2:
        name = "Al Bahr al Ahmar";
        break;
        case 3:
        name = "Al Buhayrah";
        break;
        case 4:
        name = "Al Fayyum";
        break;
        case 5:
        name = "Al Gharbiyah";
        break;
        case 6:
        name = "Al Iskandariyah";
        break;
        case 7:
        name = "Al Isma'iliyah";
        break;
        case 8:
        name = "Al Jizah";
        break;
        case 9:
        name = "Al Minufiyah";
        break;
        case 10:
        name = "Al Minya";
        break;
        case 11:
        name = "Al Qahirah";
        break;
        case 12:
        name = "Al Qalyubiyah";
        break;
        case 13:
        name = "Al Wadi al Jadid";
        break;
        case 14:
        name = "Ash Sharqiyah";
        break;
        case 15:
        name = "As Suways";
        break;
        case 16:
        name = "Aswan";
        break;
        case 17:
        name = "Asyut";
        break;
        case 18:
        name = "Bani Suwayf";
        break;
        case 19:
        name = "Bur Sa'id";
        break;
        case 20:
        name = "Dumyat";
        break;
        case 21:
        name = "Kafr ash Shaykh";
        break;
        case 22:
        name = "Matruh";
        break;
        case 23:
        name = "Qina";
        break;
        case 24:
        name = "Suhaj";
        break;
        case 26:
        name = "Janub Sina'";
        break;
        case 27:
        name = "Shamal Sina'";
        break;
      }
    }
    if (country_code.equals("ER") == true) {
      switch (region_code2) {
        case 1:
        name = "Anseba";
        break;
        case 2:
        name = "Debub";
        break;
        case 3:
        name = "Debubawi K'eyih Bahri";
        break;
        case 4:
        name = "Gash Barka";
        break;
        case 5:
        name = "Ma'akel";
        break;
        case 6:
        name = "Semenawi K'eyih Bahri";
        break;
      }
    }
    if (country_code.equals("ES") == true) {
      switch (region_code2) {
        case 7:
        name = "Islas Baleares";
        break;
        case 27:
        name = "La Rioja";
        break;
        case 29:
        name = "Madrid";
        break;
        case 31:
        name = "Murcia";
        break;
        case 32:
        name = "Navarra";
        break;
        case 34:
        name = "Asturias";
        break;
        case 39:
        name = "Cantabria";
        break;
        case 51:
        name = "Andalucia";
        break;
        case 52:
        name = "Aragon";
        break;
        case 53:
        name = "Canarias";
        break;
        case 54:
        name = "Castilla-La Mancha";
        break;
        case 55:
        name = "Castilla y Leon";
        break;
        case 56:
        name = "Catalonia";
        break;
        case 57:
        name = "Extremadura";
        break;
        case 58:
        name = "Galicia";
        break;
        case 59:
        name = "Pais Vasco";
        break;
        case 60:
        name = "Comunidad Valenciana";
        break;
      }
    }
    if (country_code.equals("ET") == true) {
      switch (region_code2) {
        case 2:
        name = "Amhara";
        break;
        case 7:
        name = "Somali";
        break;
        case 8:
        name = "Gambella";
        break;
        case 10:
        name = "Addis Abeba";
        break;
        case 11:
        name = "Southern";
        break;
        case 12:
        name = "Tigray";
        break;
        case 13:
        name = "Benishangul";
        break;
        case 14:
        name = "Afar";
        break;
        case 44:
        name = "Adis Abeba";
        break;
        case 45:
        name = "Afar";
        break;
        case 46:
        name = "Amara";
        break;
        case 47:
        name = "Binshangul Gumuz";
        break;
        case 48:
        name = "Dire Dawa";
        break;
        case 49:
        name = "Gambela Hizboch";
        break;
        case 50:
        name = "Hareri Hizb";
        break;
        case 51:
        name = "Oromiya";
        break;
        case 52:
        name = "Sumale";
        break;
        case 53:
        name = "Tigray";
        break;
        case 54:
        name = "YeDebub Biheroch Bihereseboch na Hizboch";
        break;
      }
    }
    if (country_code.equals("FI") == true) {
      switch (region_code2) {
        case 1:
        name = "Aland";
        break;
        case 6:
        name = "Lapland";
        break;
        case 8:
        name = "Oulu";
        break;
        case 13:
        name = "Southern Finland";
        break;
        case 14:
        name = "Eastern Finland";
        break;
        case 15:
        name = "Western Finland";
        break;
      }
    }
    if (country_code.equals("FJ") == true) {
      switch (region_code2) {
        case 1:
        name = "Central";
        break;
        case 2:
        name = "Eastern";
        break;
        case 3:
        name = "Northern";
        break;
        case 4:
        name = "Rotuma";
        break;
        case 5:
        name = "Western";
        break;
      }
    }
    if (country_code.equals("FM") == true) {
      switch (region_code2) {
        case 1:
        name = "Kosrae";
        break;
        case 2:
        name = "Pohnpei";
        break;
        case 3:
        name = "Chuuk";
        break;
        case 4:
        name = "Yap";
        break;
      }
    }
    if (country_code.equals("FR") == true) {
      switch (region_code2) {
        case 97:
        name = "Aquitaine";
        break;
        case 98:
        name = "Auvergne";
        break;
        case 99:
        name = "Basse-Normandie";
        break;
        case 832:
        name = "Bourgogne";
        break;
        case 833:
        name = "Bretagne";
        break;
        case 834:
        name = "Centre";
        break;
        case 835:
        name = "Champagne-Ardenne";
        break;
        case 836:
        name = "Corse";
        break;
        case 837:
        name = "Franche-Comte";
        break;
        case 838:
        name = "Haute-Normandie";
        break;
        case 839:
        name = "Ile-de-France";
        break;
        case 840:
        name = "Languedoc-Roussillon";
        break;
        case 875:
        name = "Limousin";
        break;
        case 876:
        name = "Lorraine";
        break;
        case 877:
        name = "Midi-Pyrenees";
        break;
        case 878:
        name = "Nord-Pas-de-Calais";
        break;
        case 879:
        name = "Pays de la Loire";
        break;
        case 880:
        name = "Picardie";
        break;
        case 881:
        name = "Poitou-Charentes";
        break;
        case 882:
        name = "Provence-Alpes-Cote d'Azur";
        break;
        case 883:
        name = "Rhone-Alpes";
        break;
        case 918:
        name = "Alsace";
        break;
      }
    }
    if (country_code.equals("GA") == true) {
      switch (region_code2) {
        case 1:
        name = "Estuaire";
        break;
        case 2:
        name = "Haut-Ogooue";
        break;
        case 3:
        name = "Moyen-Ogooue";
        break;
        case 4:
        name = "Ngounie";
        break;
        case 5:
        name = "Nyanga";
        break;
        case 6:
        name = "Ogooue-Ivindo";
        break;
        case 7:
        name = "Ogooue-Lolo";
        break;
        case 8:
        name = "Ogooue-Maritime";
        break;
        case 9:
        name = "Woleu-Ntem";
        break;
      }
    }
    if (country_code.equals("GB") == true) {
      switch (region_code2) {
        case 1:
        name = "Avon";
        break;
        case 3:
        name = "Berkshire";
        break;
        case 7:
        name = "Cleveland";
        break;
        case 17:
        name = "Greater London";
        break;
        case 18:
        name = "Greater Manchester";
        break;
        case 20:
        name = "Hereford and Worcester";
        break;
        case 22:
        name = "Humberside";
        break;
        case 28:
        name = "Merseyside";
        break;
        case 37:
        name = "South Yorkshire";
        break;
        case 41:
        name = "Tyne and Wear";
        break;
        case 43:
        name = "West Midlands";
        break;
        case 45:
        name = "West Yorkshire";
        break;
        case 79:
        name = "Central";
        break;
        case 82:
        name = "Grampian";
        break;
        case 84:
        name = "Lothian";
        break;
        case 87:
        name = "Strathclyde";
        break;
        case 88:
        name = "Tayside";
        break;
        case 90:
        name = "Clwyd";
        break;
        case 91:
        name = "Dyfed";
        break;
        case 92:
        name = "Gwent";
        break;
        case 94:
        name = "Mid Glamorgan";
        break;
        case 96:
        name = "South Glamorgan";
        break;
        case 97:
        name = "West Glamorgan";
        break;
        case 832:
        name = "Barking and Dagenham";
        break;
        case 833:
        name = "Barnet";
        break;
        case 834:
        name = "Barnsley";
        break;
        case 835:
        name = "Bath and North East Somerset";
        break;
        case 836:
        name = "Bedfordshire";
        break;
        case 837:
        name = "Bexley";
        break;
        case 838:
        name = "Birmingham";
        break;
        case 839:
        name = "Blackburn with Darwen";
        break;
        case 840:
        name = "Blackpool";
        break;
        case 875:
        name = "Bolton";
        break;
        case 876:
        name = "Bournemouth";
        break;
        case 877:
        name = "Bracknell Forest";
        break;
        case 878:
        name = "Bradford";
        break;
        case 879:
        name = "Brent";
        break;
        case 880:
        name = "Brighton and Hove";
        break;
        case 881:
        name = "Bristol";
        break;
        case 882:
        name = "Bromley";
        break;
        case 883:
        name = "Buckinghamshire";
        break;
        case 918:
        name = "Bury";
        break;
        case 919:
        name = "Calderdale";
        break;
        case 920:
        name = "Cambridgeshire";
        break;
        case 921:
        name = "Camden";
        break;
        case 922:
        name = "Cheshire";
        break;
        case 923:
        name = "Cornwall";
        break;
        case 924:
        name = "Coventry";
        break;
        case 925:
        name = "Croydon";
        break;
        case 926:
        name = "Cumbria";
        break;
        case 961:
        name = "Darlington";
        break;
        case 962:
        name = "Derby";
        break;
        case 963:
        name = "Derbyshire";
        break;
        case 964:
        name = "Devon";
        break;
        case 965:
        name = "Doncaster";
        break;
        case 966:
        name = "Dorset";
        break;
        case 967:
        name = "Dudley";
        break;
        case 968:
        name = "Durham";
        break;
        case 969:
        name = "Ealing";
        break;
        case 1004:
        name = "East Riding of Yorkshire";
        break;
        case 1005:
        name = "East Sussex";
        break;
        case 1006:
        name = "Enfield";
        break;
        case 1007:
        name = "Essex";
        break;
        case 1008:
        name = "Gateshead";
        break;
        case 1009:
        name = "Gloucestershire";
        break;
        case 1010:
        name = "Greenwich";
        break;
        case 1011:
        name = "Hackney";
        break;
        case 1012:
        name = "Halton";
        break;
        case 1047:
        name = "Hammersmith and Fulham";
        break;
        case 1048:
        name = "Hampshire";
        break;
        case 1049:
        name = "Haringey";
        break;
        case 1050:
        name = "Harrow";
        break;
        case 1051:
        name = "Hartlepool";
        break;
        case 1052:
        name = "Havering";
        break;
        case 1053:
        name = "Herefordshire";
        break;
        case 1054:
        name = "Hertford";
        break;
        case 1055:
        name = "Hillingdon";
        break;
        case 1090:
        name = "Hounslow";
        break;
        case 1091:
        name = "Isle of Wight";
        break;
        case 1092:
        name = "Islington";
        break;
        case 1093:
        name = "Kensington and Chelsea";
        break;
        case 1094:
        name = "Kent";
        break;
        case 1095:
        name = "Kingston upon Hull";
        break;
        case 1096:
        name = "Kingston upon Thames";
        break;
        case 1097:
        name = "Kirklees";
        break;
        case 1098:
        name = "Knowsley";
        break;
        case 1133:
        name = "Lambeth";
        break;
        case 1134:
        name = "Lancashire";
        break;
        case 1135:
        name = "Leeds";
        break;
        case 1136:
        name = "Leicester";
        break;
        case 1137:
        name = "Leicestershire";
        break;
        case 1138:
        name = "Lewisham";
        break;
        case 1139:
        name = "Lincolnshire";
        break;
        case 1140:
        name = "Liverpool";
        break;
        case 1141:
        name = "London";
        break;
        case 1176:
        name = "Luton";
        break;
        case 1177:
        name = "Manchester";
        break;
        case 1178:
        name = "Medway";
        break;
        case 1179:
        name = "Merton";
        break;
        case 1180:
        name = "Middlesbrough";
        break;
        case 1181:
        name = "Milton Keynes";
        break;
        case 1182:
        name = "Newcastle upon Tyne";
        break;
        case 1183:
        name = "Newham";
        break;
        case 1184:
        name = "Norfolk";
        break;
        case 1219:
        name = "Northamptonshire";
        break;
        case 1220:
        name = "North East Lincolnshire";
        break;
        case 1221:
        name = "North Lincolnshire";
        break;
        case 1222:
        name = "North Somerset";
        break;
        case 1223:
        name = "North Tyneside";
        break;
        case 1224:
        name = "Northumberland";
        break;
        case 1225:
        name = "North Yorkshire";
        break;
        case 1226:
        name = "Nottingham";
        break;
        case 1227:
        name = "Nottinghamshire";
        break;
        case 1262:
        name = "Oldham";
        break;
        case 1263:
        name = "Oxfordshire";
        break;
        case 1264:
        name = "Peterborough";
        break;
        case 1265:
        name = "Plymouth";
        break;
        case 1266:
        name = "Poole";
        break;
        case 1267:
        name = "Portsmouth";
        break;
        case 1268:
        name = "Reading";
        break;
        case 1269:
        name = "Redbridge";
        break;
        case 1270:
        name = "Redcar and Cleveland";
        break;
        case 1305:
        name = "Richmond upon Thames";
        break;
        case 1306:
        name = "Rochdale";
        break;
        case 1307:
        name = "Rotherham";
        break;
        case 1308:
        name = "Rutland";
        break;
        case 1309:
        name = "Salford";
        break;
        case 1310:
        name = "Shropshire";
        break;
        case 1311:
        name = "Sandwell";
        break;
        case 1312:
        name = "Sefton";
        break;
        case 1313:
        name = "Sheffield";
        break;
        case 1348:
        name = "Slough";
        break;
        case 1349:
        name = "Solihull";
        break;
        case 1350:
        name = "Somerset";
        break;
        case 1351:
        name = "Southampton";
        break;
        case 1352:
        name = "Southend-on-Sea";
        break;
        case 1353:
        name = "South Gloucestershire";
        break;
        case 1354:
        name = "South Tyneside";
        break;
        case 1355:
        name = "Southwark";
        break;
        case 1356:
        name = "Staffordshire";
        break;
        case 1391:
        name = "St. Helens";
        break;
        case 1392:
        name = "Stockport";
        break;
        case 1393:
        name = "Stockton-on-Tees";
        break;
        case 1394:
        name = "Stoke-on-Trent";
        break;
        case 1395:
        name = "Suffolk";
        break;
        case 1396:
        name = "Sunderland";
        break;
        case 1397:
        name = "Surrey";
        break;
        case 1398:
        name = "Sutton";
        break;
        case 1399:
        name = "Swindon";
        break;
        case 1434:
        name = "Tameside";
        break;
        case 1435:
        name = "Telford and Wrekin";
        break;
        case 1436:
        name = "Thurrock";
        break;
        case 1437:
        name = "Torbay";
        break;
        case 1438:
        name = "Tower Hamlets";
        break;
        case 1439:
        name = "Trafford";
        break;
        case 1440:
        name = "Wakefield";
        break;
        case 1441:
        name = "Walsall";
        break;
        case 1442:
        name = "Waltham Forest";
        break;
        case 1477:
        name = "Wandsworth";
        break;
        case 1478:
        name = "Warrington";
        break;
        case 1479:
        name = "Warwickshire";
        break;
        case 1480:
        name = "West Berkshire";
        break;
        case 1481:
        name = "Westminster";
        break;
        case 1482:
        name = "West Sussex";
        break;
        case 1483:
        name = "Wigan";
        break;
        case 1484:
        name = "Wiltshire";
        break;
        case 1485:
        name = "Windsor and Maidenhead";
        break;
        case 1520:
        name = "Wirral";
        break;
        case 1521:
        name = "Wokingham";
        break;
        case 1522:
        name = "Wolverhampton";
        break;
        case 1523:
        name = "Worcestershire";
        break;
        case 1524:
        name = "York";
        break;
        case 1525:
        name = "Antrim";
        break;
        case 1526:
        name = "Ards";
        break;
        case 1527:
        name = "Armagh";
        break;
        case 1528:
        name = "Ballymena";
        break;
        case 1563:
        name = "Ballymoney";
        break;
        case 1564:
        name = "Banbridge";
        break;
        case 1565:
        name = "Belfast";
        break;
        case 1566:
        name = "Carrickfergus";
        break;
        case 1567:
        name = "Castlereagh";
        break;
        case 1568:
        name = "Coleraine";
        break;
        case 1569:
        name = "Cookstown";
        break;
        case 1570:
        name = "Craigavon";
        break;
        case 1571:
        name = "Down";
        break;
        case 1606:
        name = "Dungannon";
        break;
        case 1607:
        name = "Fermanagh";
        break;
        case 1608:
        name = "Larne";
        break;
        case 1609:
        name = "Limavady";
        break;
        case 1610:
        name = "Lisburn";
        break;
        case 1611:
        name = "Derry";
        break;
        case 1612:
        name = "Magherafelt";
        break;
        case 1613:
        name = "Moyle";
        break;
        case 1614:
        name = "Newry and Mourne";
        break;
        case 1649:
        name = "Newtownabbey";
        break;
        case 1650:
        name = "North Down";
        break;
        case 1651:
        name = "Omagh";
        break;
        case 1652:
        name = "Strabane";
        break;
        case 1653:
        name = "Aberdeen City";
        break;
        case 1654:
        name = "Aberdeenshire";
        break;
        case 1655:
        name = "Angus";
        break;
        case 1656:
        name = "Argyll and Bute";
        break;
        case 1657:
        name = "Scottish Borders";
        break;
        case 1692:
        name = "Clackmannanshire";
        break;
        case 1693:
        name = "Dumfries and Galloway";
        break;
        case 1694:
        name = "Dundee City";
        break;
        case 1695:
        name = "East Ayrshire";
        break;
        case 1696:
        name = "East Dunbartonshire";
        break;
        case 1697:
        name = "East Lothian";
        break;
        case 1698:
        name = "East Renfrewshire";
        break;
        case 1699:
        name = "Edinburgh";
        break;
        case 1700:
        name = "Falkirk";
        break;
        case 1735:
        name = "Fife";
        break;
        case 1736:
        name = "Glasgow City";
        break;
        case 1737:
        name = "Highland";
        break;
        case 1738:
        name = "Inverclyde";
        break;
        case 1739:
        name = "Midlothian";
        break;
        case 1740:
        name = "Moray";
        break;
        case 1741:
        name = "North Ayrshire";
        break;
        case 1742:
        name = "North Lanarkshire";
        break;
        case 1743:
        name = "Orkney";
        break;
        case 1778:
        name = "Perth and Kinross";
        break;
        case 1779:
        name = "Renfrewshire";
        break;
        case 1780:
        name = "Shetland Islands";
        break;
        case 1781:
        name = "South Ayrshire";
        break;
        case 1782:
        name = "South Lanarkshire";
        break;
        case 1783:
        name = "Stirling";
        break;
        case 1784:
        name = "West Dunbartonshire";
        break;
        case 1785:
        name = "Eilean Siar";
        break;
        case 1786:
        name = "West Lothian";
        break;
        case 1821:
        name = "Isle of Anglesey";
        break;
        case 1822:
        name = "Blaenau Gwent";
        break;
        case 1823:
        name = "Bridgend";
        break;
        case 1824:
        name = "Caerphilly";
        break;
        case 1825:
        name = "Cardiff";
        break;
        case 1826:
        name = "Ceredigion";
        break;
        case 1827:
        name = "Carmarthenshire";
        break;
        case 1828:
        name = "Conwy";
        break;
        case 1829:
        name = "Denbighshire";
        break;
        case 1864:
        name = "Flintshire";
        break;
        case 1865:
        name = "Gwynedd";
        break;
        case 1866:
        name = "Merthyr Tydfil";
        break;
        case 1867:
        name = "Monmouthshire";
        break;
        case 1868:
        name = "Neath Port Talbot";
        break;
        case 1869:
        name = "Newport";
        break;
        case 1870:
        name = "Pembrokeshire";
        break;
        case 1871:
        name = "Powys";
        break;
        case 1872:
        name = "Rhondda Cynon Taff";
        break;
        case 1907:
        name = "Swansea";
        break;
        case 1908:
        name = "Torfaen";
        break;
        case 1909:
        name = "Vale of Glamorgan";
        break;
        case 1910:
        name = "Wrexham";
        break;
      }
    }
    if (country_code.equals("GD") == true) {
      switch (region_code2) {
        case 1:
        name = "Saint Andrew";
        break;
        case 2:
        name = "Saint David";
        break;
        case 3:
        name = "Saint George";
        break;
        case 4:
        name = "Saint John";
        break;
        case 5:
        name = "Saint Mark";
        break;
        case 6:
        name = "Saint Patrick";
        break;
      }
    }
    if (country_code.equals("GE") == true) {
      switch (region_code2) {
        case 1:
        name = "Abashis Raioni";
        break;
        case 2:
        name = "Abkhazia";
        break;
        case 3:
        name = "Adigenis Raioni";
        break;
        case 4:
        name = "Ajaria";
        break;
        case 5:
        name = "Akhalgoris Raioni";
        break;
        case 6:
        name = "Akhalk'alak'is Raioni";
        break;
        case 7:
        name = "Akhalts'ikhis Raioni";
        break;
        case 8:
        name = "Akhmetis Raioni";
        break;
        case 9:
        name = "Ambrolauris Raioni";
        break;
        case 10:
        name = "Aspindzis Raioni";
        break;
        case 11:
        name = "Baghdat'is Raioni";
        break;
        case 12:
        name = "Bolnisis Raioni";
        break;
        case 13:
        name = "Borjomis Raioni";
        break;
        case 14:
        name = "Chiat'ura";
        break;
        case 15:
        name = "Ch'khorotsqus Raioni";
        break;
        case 16:
        name = "Ch'okhatauris Raioni";
        break;
        case 17:
        name = "Dedop'listsqaros Raioni";
        break;
        case 18:
        name = "Dmanisis Raioni";
        break;
        case 19:
        name = "Dushet'is Raioni";
        break;
        case 20:
        name = "Gardabanis Raioni";
        break;
        case 21:
        name = "Gori";
        break;
        case 22:
        name = "Goris Raioni";
        break;
        case 23:
        name = "Gurjaanis Raioni";
        break;
        case 24:
        name = "Javis Raioni";
        break;
        case 25:
        name = "K'arelis Raioni";
        break;
        case 26:
        name = "Kaspis Raioni";
        break;
        case 27:
        name = "Kharagaulis Raioni";
        break;
        case 28:
        name = "Khashuris Raioni";
        break;
        case 29:
        name = "Khobis Raioni";
        break;
        case 30:
        name = "Khonis Raioni";
        break;
        case 31:
        name = "K'ut'aisi";
        break;
        case 32:
        name = "Lagodekhis Raioni";
        break;
        case 33:
        name = "Lanch'khut'is Raioni";
        break;
        case 34:
        name = "Lentekhis Raioni";
        break;
        case 35:
        name = "Marneulis Raioni";
        break;
        case 36:
        name = "Martvilis Raioni";
        break;
        case 37:
        name = "Mestiis Raioni";
        break;
        case 38:
        name = "Mts'khet'is Raioni";
        break;
        case 39:
        name = "Ninotsmindis Raioni";
        break;
        case 40:
        name = "Onis Raioni";
        break;
        case 41:
        name = "Ozurget'is Raioni";
        break;
        case 42:
        name = "P'ot'i";
        break;
        case 43:
        name = "Qazbegis Raioni";
        break;
        case 44:
        name = "Qvarlis Raioni";
        break;
        case 45:
        name = "Rust'avi";
        break;
        case 46:
        name = "Sach'kheris Raioni";
        break;
        case 47:
        name = "Sagarejos Raioni";
        break;
        case 48:
        name = "Samtrediis Raioni";
        break;
        case 49:
        name = "Senakis Raioni";
        break;
        case 50:
        name = "Sighnaghis Raioni";
        break;
        case 51:
        name = "T'bilisi";
        break;
        case 52:
        name = "T'elavis Raioni";
        break;
        case 53:
        name = "T'erjolis Raioni";
        break;
        case 54:
        name = "T'et'ritsqaros Raioni";
        break;
        case 55:
        name = "T'ianet'is Raioni";
        break;
        case 56:
        name = "Tqibuli";
        break;
        case 57:
        name = "Ts'ageris Raioni";
        break;
        case 58:
        name = "Tsalenjikhis Raioni";
        break;
        case 59:
        name = "Tsalkis Raioni";
        break;
        case 60:
        name = "Tsqaltubo";
        break;
        case 61:
        name = "Vanis Raioni";
        break;
        case 62:
        name = "Zestap'onis Raioni";
        break;
        case 63:
        name = "Zugdidi";
        break;
        case 64:
        name = "Zugdidis Raioni";
        break;
      }
    }
    if (country_code.equals("GH") == true) {
      switch (region_code2) {
        case 1:
        name = "Greater Accra";
        break;
        case 2:
        name = "Ashanti";
        break;
        case 3:
        name = "Brong-Ahafo";
        break;
        case 4:
        name = "Central";
        break;
        case 5:
        name = "Eastern";
        break;
        case 6:
        name = "Northern";
        break;
        case 8:
        name = "Volta";
        break;
        case 9:
        name = "Western";
        break;
        case 10:
        name = "Upper East";
        break;
        case 11:
        name = "Upper West";
        break;
      }
    }
    if (country_code.equals("GL") == true) {
      switch (region_code2) {
        case 1:
        name = "Nordgronland";
        break;
        case 2:
        name = "Ostgronland";
        break;
        case 3:
        name = "Vestgronland";
        break;
      }
    }
    if (country_code.equals("GM") == true) {
      switch (region_code2) {
        case 1:
        name = "Banjul";
        break;
        case 2:
        name = "Lower River";
        break;
        case 3:
        name = "Central River";
        break;
        case 4:
        name = "Upper River";
        break;
        case 5:
        name = "Western";
        break;
        case 7:
        name = "North Bank";
        break;
      }
    }
    if (country_code.equals("GN") == true) {
      switch (region_code2) {
        case 1:
        name = "Beyla";
        break;
        case 2:
        name = "Boffa";
        break;
        case 3:
        name = "Boke";
        break;
        case 4:
        name = "Conakry";
        break;
        case 5:
        name = "Dabola";
        break;
        case 6:
        name = "Dalaba";
        break;
        case 7:
        name = "Dinguiraye";
        break;
        case 9:
        name = "Faranah";
        break;
        case 10:
        name = "Forecariah";
        break;
        case 11:
        name = "Fria";
        break;
        case 12:
        name = "Gaoual";
        break;
        case 13:
        name = "Gueckedou";
        break;
        case 15:
        name = "Kerouane";
        break;
        case 16:
        name = "Kindia";
        break;
        case 17:
        name = "Kissidougou";
        break;
        case 18:
        name = "Koundara";
        break;
        case 19:
        name = "Kouroussa";
        break;
        case 21:
        name = "Macenta";
        break;
        case 22:
        name = "Mali";
        break;
        case 23:
        name = "Mamou";
        break;
        case 25:
        name = "Pita";
        break;
        case 27:
        name = "Telimele";
        break;
        case 28:
        name = "Tougue";
        break;
        case 29:
        name = "Yomou";
        break;
        case 30:
        name = "Coyah";
        break;
        case 31:
        name = "Dubreka";
        break;
        case 32:
        name = "Kankan";
        break;
        case 33:
        name = "Koubia";
        break;
        case 34:
        name = "Labe";
        break;
        case 35:
        name = "Lelouma";
        break;
        case 36:
        name = "Lola";
        break;
        case 37:
        name = "Mandiana";
        break;
        case 38:
        name = "Nzerekore";
        break;
        case 39:
        name = "Siguiri";
        break;
      }
    }
    if (country_code.equals("GQ") == true) {
      switch (region_code2) {
        case 3:
        name = "Annobon";
        break;
        case 4:
        name = "Bioko Norte";
        break;
        case 5:
        name = "Bioko Sur";
        break;
        case 6:
        name = "Centro Sur";
        break;
        case 7:
        name = "Kie-Ntem";
        break;
        case 8:
        name = "Litoral";
        break;
        case 9:
        name = "Wele-Nzas";
        break;
      }
    }
    if (country_code.equals("GR") == true) {
      switch (region_code2) {
        case 1:
        name = "Evros";
        break;
        case 2:
        name = "Rodhopi";
        break;
        case 3:
        name = "Xanthi";
        break;
        case 4:
        name = "Drama";
        break;
        case 5:
        name = "Serrai";
        break;
        case 6:
        name = "Kilkis";
        break;
        case 7:
        name = "Pella";
        break;
        case 8:
        name = "Florina";
        break;
        case 9:
        name = "Kastoria";
        break;
        case 10:
        name = "Grevena";
        break;
        case 11:
        name = "Kozani";
        break;
        case 12:
        name = "Imathia";
        break;
        case 13:
        name = "Thessaloniki";
        break;
        case 14:
        name = "Kavala";
        break;
        case 15:
        name = "Khalkidhiki";
        break;
        case 16:
        name = "Pieria";
        break;
        case 17:
        name = "Ioannina";
        break;
        case 18:
        name = "Thesprotia";
        break;
        case 19:
        name = "Preveza";
        break;
        case 20:
        name = "Arta";
        break;
        case 21:
        name = "Larisa";
        break;
        case 22:
        name = "Trikala";
        break;
        case 23:
        name = "Kardhitsa";
        break;
        case 24:
        name = "Magnisia";
        break;
        case 25:
        name = "Kerkira";
        break;
        case 26:
        name = "Levkas";
        break;
        case 27:
        name = "Kefallinia";
        break;
        case 28:
        name = "Zakinthos";
        break;
        case 29:
        name = "Fthiotis";
        break;
        case 30:
        name = "Evritania";
        break;
        case 31:
        name = "Aitolia kai Akarnania";
        break;
        case 32:
        name = "Fokis";
        break;
        case 33:
        name = "Voiotia";
        break;
        case 34:
        name = "Evvoia";
        break;
        case 35:
        name = "Attiki";
        break;
        case 36:
        name = "Argolis";
        break;
        case 37:
        name = "Korinthia";
        break;
        case 38:
        name = "Akhaia";
        break;
        case 39:
        name = "Ilia";
        break;
        case 40:
        name = "Messinia";
        break;
        case 41:
        name = "Arkadhia";
        break;
        case 42:
        name = "Lakonia";
        break;
        case 43:
        name = "Khania";
        break;
        case 44:
        name = "Rethimni";
        break;
        case 45:
        name = "Iraklion";
        break;
        case 46:
        name = "Lasithi";
        break;
        case 47:
        name = "Dhodhekanisos";
        break;
        case 48:
        name = "Samos";
        break;
        case 49:
        name = "Kikladhes";
        break;
        case 50:
        name = "Khios";
        break;
        case 51:
        name = "Lesvos";
        break;
      }
    }
    if (country_code.equals("GT") == true) {
      switch (region_code2) {
        case 1:
        name = "Alta Verapaz";
        break;
        case 2:
        name = "Baja Verapaz";
        break;
        case 3:
        name = "Chimaltenango";
        break;
        case 4:
        name = "Chiquimula";
        break;
        case 5:
        name = "El Progreso";
        break;
        case 6:
        name = "Escuintla";
        break;
        case 7:
        name = "Guatemala";
        break;
        case 8:
        name = "Huehuetenango";
        break;
        case 9:
        name = "Izabal";
        break;
        case 10:
        name = "Jalapa";
        break;
        case 11:
        name = "Jutiapa";
        break;
        case 12:
        name = "Peten";
        break;
        case 13:
        name = "Quetzaltenango";
        break;
        case 14:
        name = "Quiche";
        break;
        case 15:
        name = "Retalhuleu";
        break;
        case 16:
        name = "Sacatepequez";
        break;
        case 17:
        name = "San Marcos";
        break;
        case 18:
        name = "Santa Rosa";
        break;
        case 19:
        name = "Solola";
        break;
        case 20:
        name = "Suchitepequez";
        break;
        case 21:
        name = "Totonicapan";
        break;
        case 22:
        name = "Zacapa";
        break;
      }
    }
    if (country_code.equals("GW") == true) {
      switch (region_code2) {
        case 1:
        name = "Bafata";
        break;
        case 2:
        name = "Quinara";
        break;
        case 4:
        name = "Oio";
        break;
        case 5:
        name = "Bolama";
        break;
        case 6:
        name = "Cacheu";
        break;
        case 7:
        name = "Tombali";
        break;
        case 10:
        name = "Gabu";
        break;
        case 11:
        name = "Bissau";
        break;
        case 12:
        name = "Biombo";
        break;
      }
    }
    if (country_code.equals("GY") == true) {
      switch (region_code2) {
        case 10:
        name = "Barima-Waini";
        break;
        case 11:
        name = "Cuyuni-Mazaruni";
        break;
        case 12:
        name = "Demerara-Mahaica";
        break;
        case 13:
        name = "East Berbice-Corentyne";
        break;
        case 14:
        name = "Essequibo Islands-West Demerara";
        break;
        case 15:
        name = "Mahaica-Berbice";
        break;
        case 16:
        name = "Pomeroon-Supenaam";
        break;
        case 17:
        name = "Potaro-Siparuni";
        break;
        case 18:
        name = "Upper Demerara-Berbice";
        break;
        case 19:
        name = "Upper Takutu-Upper Essequibo";
        break;
      }
    }
    if (country_code.equals("HN") == true) {
      switch (region_code2) {
        case 1:
        name = "Atlantida";
        break;
        case 2:
        name = "Choluteca";
        break;
        case 3:
        name = "Colon";
        break;
        case 4:
        name = "Comayagua";
        break;
        case 5:
        name = "Copan";
        break;
        case 6:
        name = "Cortes";
        break;
        case 7:
        name = "El Paraiso";
        break;
        case 8:
        name = "Francisco Morazan";
        break;
        case 9:
        name = "Gracias a Dios";
        break;
        case 10:
        name = "Intibuca";
        break;
        case 11:
        name = "Islas de la Bahia";
        break;
        case 12:
        name = "La Paz";
        break;
        case 13:
        name = "Lempira";
        break;
        case 14:
        name = "Ocotepeque";
        break;
        case 15:
        name = "Olancho";
        break;
        case 16:
        name = "Santa Barbara";
        break;
        case 17:
        name = "Valle";
        break;
        case 18:
        name = "Yoro";
        break;
      }
    }
    if (country_code.equals("HR") == true) {
      switch (region_code2) {
        case 1:
        name = "Bjelovarsko-Bilogorska";
        break;
        case 2:
        name = "Brodsko-Posavska";
        break;
        case 3:
        name = "Dubrovacko-Neretvanska";
        break;
        case 4:
        name = "Istarska";
        break;
        case 5:
        name = "Karlovacka";
        break;
        case 6:
        name = "Koprivnicko-Krizevacka";
        break;
        case 7:
        name = "Krapinsko-Zagorska";
        break;
        case 8:
        name = "Licko-Senjska";
        break;
        case 9:
        name = "Medimurska";
        break;
        case 10:
        name = "Osjecko-Baranjska";
        break;
        case 11:
        name = "Pozesko-Slavonska";
        break;
        case 12:
        name = "Primorsko-Goranska";
        break;
        case 13:
        name = "Sibensko-Kninska";
        break;
        case 14:
        name = "Sisacko-Moslavacka";
        break;
        case 15:
        name = "Splitsko-Dalmatinska";
        break;
        case 16:
        name = "Varazdinska";
        break;
        case 17:
        name = "Viroviticko-Podravska";
        break;
        case 18:
        name = "Vukovarsko-Srijemska";
        break;
        case 19:
        name = "Zadarska";
        break;
        case 20:
        name = "Zagrebacka";
        break;
        case 21:
        name = "Grad Zagreb";
        break;
      }
    }
    if (country_code.equals("HT") == true) {
      switch (region_code2) {
        case 3:
        name = "Nord-Ouest";
        break;
        case 6:
        name = "Artibonite";
        break;
        case 7:
        name = "Centre";
        break;
        case 9:
        name = "Nord";
        break;
        case 10:
        name = "Nord-Est";
        break;
        case 11:
        name = "Ouest";
        break;
        case 12:
        name = "Sud";
        break;
        case 13:
        name = "Sud-Est";
        break;
        case 14:
        name = "Grand' Anse";
        break;
        case 15:
        name = "Nippes";
        break;
      }
    }
    if (country_code.equals("HU") == true) {
      switch (region_code2) {
        case 1:
        name = "Bacs-Kiskun";
        break;
        case 2:
        name = "Baranya";
        break;
        case 3:
        name = "Bekes";
        break;
        case 4:
        name = "Borsod-Abauj-Zemplen";
        break;
        case 5:
        name = "Budapest";
        break;
        case 6:
        name = "Csongrad";
        break;
        case 7:
        name = "Debrecen";
        break;
        case 8:
        name = "Fejer";
        break;
        case 9:
        name = "Gyor-Moson-Sopron";
        break;
        case 10:
        name = "Hajdu-Bihar";
        break;
        case 11:
        name = "Heves";
        break;
        case 12:
        name = "Komarom-Esztergom";
        break;
        case 13:
        name = "Miskolc";
        break;
        case 14:
        name = "Nograd";
        break;
        case 15:
        name = "Pecs";
        break;
        case 16:
        name = "Pest";
        break;
        case 17:
        name = "Somogy";
        break;
        case 18:
        name = "Szabolcs-Szatmar-Bereg";
        break;
        case 19:
        name = "Szeged";
        break;
        case 20:
        name = "Jasz-Nagykun-Szolnok";
        break;
        case 21:
        name = "Tolna";
        break;
        case 22:
        name = "Vas";
        break;
        case 23:
        name = "Veszprem";
        break;
        case 24:
        name = "Zala";
        break;
        case 25:
        name = "Gyor";
        break;
        case 26:
        name = "Bekescsaba";
        break;
        case 27:
        name = "Dunaujvaros";
        break;
        case 28:
        name = "Eger";
        break;
        case 29:
        name = "Hodmezovasarhely";
        break;
        case 30:
        name = "Kaposvar";
        break;
        case 31:
        name = "Kecskemet";
        break;
        case 32:
        name = "Nagykanizsa";
        break;
        case 33:
        name = "Nyiregyhaza";
        break;
        case 34:
        name = "Sopron";
        break;
        case 35:
        name = "Szekesfehervar";
        break;
        case 36:
        name = "Szolnok";
        break;
        case 37:
        name = "Szombathely";
        break;
        case 38:
        name = "Tatabanya";
        break;
        case 39:
        name = "Veszprem";
        break;
        case 40:
        name = "Zalaegerszeg";
        break;
        case 41:
        name = "Salgotarjan";
        break;
        case 42:
        name = "Szekszard";
        break;
      }
    }
    if (country_code.equals("ID") == true) {
      switch (region_code2) {
        case 1:
        name = "Aceh";
        break;
        case 2:
        name = "Bali";
        break;
        case 3:
        name = "Bengkulu";
        break;
        case 4:
        name = "Jakarta Raya";
        break;
        case 5:
        name = "Jambi";
        break;
        case 6:
        name = "Jawa Barat";
        break;
        case 7:
        name = "Jawa Tengah";
        break;
        case 8:
        name = "Jawa Timur";
        break;
        case 9:
        name = "Papua";
        break;
        case 10:
        name = "Yogyakarta";
        break;
        case 11:
        name = "Kalimantan Barat";
        break;
        case 12:
        name = "Kalimantan Selatan";
        break;
        case 13:
        name = "Kalimantan Tengah";
        break;
        case 14:
        name = "Kalimantan Timur";
        break;
        case 15:
        name = "Lampung";
        break;
        case 16:
        name = "Maluku";
        break;
        case 17:
        name = "Nusa Tenggara Barat";
        break;
        case 18:
        name = "Nusa Tenggara Timur";
        break;
        case 19:
        name = "Riau";
        break;
        case 20:
        name = "Sulawesi Selatan";
        break;
        case 21:
        name = "Sulawesi Tengah";
        break;
        case 22:
        name = "Sulawesi Tenggara";
        break;
        case 23:
        name = "Sulawesi Utara";
        break;
        case 24:
        name = "Sumatera Barat";
        break;
        case 25:
        name = "Sumatera Selatan";
        break;
        case 26:
        name = "Sumatera Utara";
        break;
        case 28:
        name = "Maluku";
        break;
        case 29:
        name = "Maluku Utara";
        break;
        case 30:
        name = "Jawa Barat";
        break;
        case 31:
        name = "Sulawesi Utara";
        break;
        case 32:
        name = "Sumatera Selatan";
        break;
        case 33:
        name = "Banten";
        break;
        case 34:
        name = "Gorontalo";
        break;
        case 35:
        name = "Kepulauan Bangka Belitung";
        break;
        case 36:
        name = "Papua";
        break;
        case 37:
        name = "Riau";
        break;
        case 38:
        name = "Sulawesi Selatan";
        break;
        case 39:
        name = "Irian Jaya Barat";
        break;
        case 40:
        name = "Kepulauan Riau";
        break;
        case 41:
        name = "Sulawesi Barat";
        break;
      }
    }
    if (country_code.equals("IE") == true) {
      switch (region_code2) {
        case 1:
        name = "Carlow";
        break;
        case 2:
        name = "Cavan";
        break;
        case 3:
        name = "Clare";
        break;
        case 4:
        name = "Cork";
        break;
        case 6:
        name = "Donegal";
        break;
        case 7:
        name = "Dublin";
        break;
        case 10:
        name = "Galway";
        break;
        case 11:
        name = "Kerry";
        break;
        case 12:
        name = "Kildare";
        break;
        case 13:
        name = "Kilkenny";
        break;
        case 14:
        name = "Leitrim";
        break;
        case 15:
        name = "Laois";
        break;
        case 16:
        name = "Limerick";
        break;
        case 18:
        name = "Longford";
        break;
        case 19:
        name = "Louth";
        break;
        case 20:
        name = "Mayo";
        break;
        case 21:
        name = "Meath";
        break;
        case 22:
        name = "Monaghan";
        break;
        case 23:
        name = "Offaly";
        break;
        case 24:
        name = "Roscommon";
        break;
        case 25:
        name = "Sligo";
        break;
        case 26:
        name = "Tipperary";
        break;
        case 27:
        name = "Waterford";
        break;
        case 29:
        name = "Westmeath";
        break;
        case 30:
        name = "Wexford";
        break;
        case 31:
        name = "Wicklow";
        break;
      }
    }
    if (country_code.equals("IL") == true) {
      switch (region_code2) {
        case 1:
        name = "HaDarom";
        break;
        case 2:
        name = "HaMerkaz";
        break;
        case 3:
        name = "HaZafon";
        break;
        case 4:
        name = "Hefa";
        break;
        case 5:
        name = "Tel Aviv";
        break;
        case 6:
        name = "Yerushalayim";
        break;
      }
    }
    if (country_code.equals("IN") == true) {
      switch (region_code2) {
        case 1:
        name = "Andaman and Nicobar Islands";
        break;
        case 2:
        name = "Andhra Pradesh";
        break;
        case 3:
        name = "Assam";
        break;
        case 5:
        name = "Chandigarh";
        break;
        case 6:
        name = "Dadra and Nagar Haveli";
        break;
        case 7:
        name = "Delhi";
        break;
        case 9:
        name = "Gujarat";
        break;
        case 10:
        name = "Haryana";
        break;
        case 11:
        name = "Himachal Pradesh";
        break;
        case 12:
        name = "Jammu and Kashmir";
        break;
        case 13:
        name = "Kerala";
        break;
        case 14:
        name = "Lakshadweep";
        break;
        case 16:
        name = "Maharashtra";
        break;
        case 17:
        name = "Manipur";
        break;
        case 18:
        name = "Meghalaya";
        break;
        case 19:
        name = "Karnataka";
        break;
        case 20:
        name = "Nagaland";
        break;
        case 21:
        name = "Orissa";
        break;
        case 22:
        name = "Puducherry";
        break;
        case 23:
        name = "Punjab";
        break;
        case 24:
        name = "Rajasthan";
        break;
        case 25:
        name = "Tamil Nadu";
        break;
        case 26:
        name = "Tripura";
        break;
        case 28:
        name = "West Bengal";
        break;
        case 29:
        name = "Sikkim";
        break;
        case 30:
        name = "Arunachal Pradesh";
        break;
        case 31:
        name = "Mizoram";
        break;
        case 32:
        name = "Daman and Diu";
        break;
        case 33:
        name = "Goa";
        break;
        case 34:
        name = "Bihar";
        break;
        case 35:
        name = "Madhya Pradesh";
        break;
        case 36:
        name = "Uttar Pradesh";
        break;
        case 37:
        name = "Chhattisgarh";
        break;
        case 38:
        name = "Jharkhand";
        break;
        case 39:
        name = "Uttarakhand";
        break;
      }
    }
    if (country_code.equals("IQ") == true) {
      switch (region_code2) {
        case 1:
        name = "Al Anbar";
        break;
        case 2:
        name = "Al Basrah";
        break;
        case 3:
        name = "Al Muthanna";
        break;
        case 4:
        name = "Al Qadisiyah";
        break;
        case 5:
        name = "As Sulaymaniyah";
        break;
        case 6:
        name = "Babil";
        break;
        case 7:
        name = "Baghdad";
        break;
        case 8:
        name = "Dahuk";
        break;
        case 9:
        name = "Dhi Qar";
        break;
        case 10:
        name = "Diyala";
        break;
        case 11:
        name = "Arbil";
        break;
        case 12:
        name = "Karbala'";
        break;
        case 13:
        name = "At Ta'mim";
        break;
        case 14:
        name = "Maysan";
        break;
        case 15:
        name = "Ninawa";
        break;
        case 16:
        name = "Wasit";
        break;
        case 17:
        name = "An Najaf";
        break;
        case 18:
        name = "Salah ad Din";
        break;
      }
    }
    if (country_code.equals("IR") == true) {
      switch (region_code2) {
        case 1:
        name = "Azarbayjan-e Bakhtari";
        break;
        case 2:
        name = "Azarbayjan-e Khavari";
        break;
        case 3:
        name = "Chahar Mahall va Bakhtiari";
        break;
        case 4:
        name = "Sistan va Baluchestan";
        break;
        case 5:
        name = "Kohkiluyeh va Buyer Ahmadi";
        break;
        case 7:
        name = "Fars";
        break;
        case 8:
        name = "Gilan";
        break;
        case 9:
        name = "Hamadan";
        break;
        case 10:
        name = "Ilam";
        break;
        case 11:
        name = "Hormozgan";
        break;
        case 12:
        name = "Kerman";
        break;
        case 13:
        name = "Bakhtaran";
        break;
        case 15:
        name = "Khuzestan";
        break;
        case 16:
        name = "Kordestan";
        break;
        case 17:
        name = "Mazandaran";
        break;
        case 18:
        name = "Semnan Province";
        break;
        case 19:
        name = "Markazi";
        break;
        case 21:
        name = "Zanjan";
        break;
        case 22:
        name = "Bushehr";
        break;
        case 23:
        name = "Lorestan";
        break;
        case 24:
        name = "Markazi";
        break;
        case 25:
        name = "Semnan";
        break;
        case 26:
        name = "Tehran";
        break;
        case 27:
        name = "Zanjan";
        break;
        case 28:
        name = "Esfahan";
        break;
        case 29:
        name = "Kerman";
        break;
        case 30:
        name = "Khorasan";
        break;
        case 31:
        name = "Yazd";
        break;
        case 32:
        name = "Ardabil";
        break;
        case 33:
        name = "East Azarbaijan";
        break;
        case 34:
        name = "Markazi";
        break;
        case 35:
        name = "Mazandaran";
        break;
        case 36:
        name = "Zanjan";
        break;
        case 37:
        name = "Golestan";
        break;
        case 38:
        name = "Qazvin";
        break;
        case 39:
        name = "Qom";
        break;
        case 40:
        name = "Yazd";
        break;
        case 41:
        name = "Khorasan-e Janubi";
        break;
        case 42:
        name = "Khorasan-e Razavi";
        break;
        case 43:
        name = "Khorasan-e Shemali";
        break;
      }
    }
    if (country_code.equals("IS") == true) {
      switch (region_code2) {
        case 3:
        name = "Arnessysla";
        break;
        case 5:
        name = "Austur-Hunavatnssysla";
        break;
        case 6:
        name = "Austur-Skaftafellssysla";
        break;
        case 7:
        name = "Borgarfjardarsysla";
        break;
        case 9:
        name = "Eyjafjardarsysla";
        break;
        case 10:
        name = "Gullbringusysla";
        break;
        case 15:
        name = "Kjosarsysla";
        break;
        case 17:
        name = "Myrasysla";
        break;
        case 20:
        name = "Nordur-Mulasysla";
        break;
        case 21:
        name = "Nordur-Tingeyjarsysla";
        break;
        case 23:
        name = "Rangarvallasysla";
        break;
        case 28:
        name = "Skagafjardarsysla";
        break;
        case 29:
        name = "Snafellsnes- og Hnappadalssysla";
        break;
        case 30:
        name = "Strandasysla";
        break;
        case 31:
        name = "Sudur-Mulasysla";
        break;
        case 32:
        name = "Sudur-Tingeyjarsysla";
        break;
        case 34:
        name = "Vestur-Bardastrandarsysla";
        break;
        case 35:
        name = "Vestur-Hunavatnssysla";
        break;
        case 36:
        name = "Vestur-Isafjardarsysla";
        break;
        case 37:
        name = "Vestur-Skaftafellssysla";
        break;
        case 40:
        name = "Norourland Eystra";
        break;
        case 41:
        name = "Norourland Vestra";
        break;
        case 42:
        name = "Suourland";
        break;
        case 43:
        name = "Suournes";
        break;
        case 44:
        name = "Vestfiroir";
        break;
        case 45:
        name = "Vesturland";
        break;
      }
    }
    if (country_code.equals("IT") == true) {
      switch (region_code2) {
        case 1:
        name = "Abruzzi";
        break;
        case 2:
        name = "Basilicata";
        break;
        case 3:
        name = "Calabria";
        break;
        case 4:
        name = "Campania";
        break;
        case 5:
        name = "Emilia-Romagna";
        break;
        case 6:
        name = "Friuli-Venezia Giulia";
        break;
        case 7:
        name = "Lazio";
        break;
        case 8:
        name = "Liguria";
        break;
        case 9:
        name = "Lombardia";
        break;
        case 10:
        name = "Marche";
        break;
        case 11:
        name = "Molise";
        break;
        case 12:
        name = "Piemonte";
        break;
        case 13:
        name = "Puglia";
        break;
        case 14:
        name = "Sardegna";
        break;
        case 15:
        name = "Sicilia";
        break;
        case 16:
        name = "Toscana";
        break;
        case 17:
        name = "Trentino-Alto Adige";
        break;
        case 18:
        name = "Umbria";
        break;
        case 19:
        name = "Valle d'Aosta";
        break;
        case 20:
        name = "Veneto";
        break;
      }
    }
    if (country_code.equals("JM") == true) {
      switch (region_code2) {
        case 1:
        name = "Clarendon";
        break;
        case 2:
        name = "Hanover";
        break;
        case 4:
        name = "Manchester";
        break;
        case 7:
        name = "Portland";
        break;
        case 8:
        name = "Saint Andrew";
        break;
        case 9:
        name = "Saint Ann";
        break;
        case 10:
        name = "Saint Catherine";
        break;
        case 11:
        name = "Saint Elizabeth";
        break;
        case 12:
        name = "Saint James";
        break;
        case 13:
        name = "Saint Mary";
        break;
        case 14:
        name = "Saint Thomas";
        break;
        case 15:
        name = "Trelawny";
        break;
        case 16:
        name = "Westmoreland";
        break;
        case 17:
        name = "Kingston";
        break;
      }
    }
    if (country_code.equals("JO") == true) {
      switch (region_code2) {
        case 2:
        name = "Al Balqa'";
        break;
        case 7:
        name = "Ma";
        break;
        case 9:
        name = "Al Karak";
        break;
        case 10:
        name = "Al Mafraq";
        break;
        case 11:
        name = "Amman Governorate";
        break;
        case 12:
        name = "At Tafilah";
        break;
        case 13:
        name = "Az Zarqa";
        break;
        case 14:
        name = "Irbid";
        break;
        case 16:
        name = "Amman";
        break;
      }
    }
    if (country_code.equals("JP") == true) {
      switch (region_code2) {
        case 1:
        name = "Aichi";
        break;
        case 2:
        name = "Akita";
        break;
        case 3:
        name = "Aomori";
        break;
        case 4:
        name = "Chiba";
        break;
        case 5:
        name = "Ehime";
        break;
        case 6:
        name = "Fukui";
        break;
        case 7:
        name = "Fukuoka";
        break;
        case 8:
        name = "Fukushima";
        break;
        case 9:
        name = "Gifu";
        break;
        case 10:
        name = "Gumma";
        break;
        case 11:
        name = "Hiroshima";
        break;
        case 12:
        name = "Hokkaido";
        break;
        case 13:
        name = "Hyogo";
        break;
        case 14:
        name = "Ibaraki";
        break;
        case 15:
        name = "Ishikawa";
        break;
        case 16:
        name = "Iwate";
        break;
        case 17:
        name = "Kagawa";
        break;
        case 18:
        name = "Kagoshima";
        break;
        case 19:
        name = "Kanagawa";
        break;
        case 20:
        name = "Kochi";
        break;
        case 21:
        name = "Kumamoto";
        break;
        case 22:
        name = "Kyoto";
        break;
        case 23:
        name = "Mie";
        break;
        case 24:
        name = "Miyagi";
        break;
        case 25:
        name = "Miyazaki";
        break;
        case 26:
        name = "Nagano";
        break;
        case 27:
        name = "Nagasaki";
        break;
        case 28:
        name = "Nara";
        break;
        case 29:
        name = "Niigata";
        break;
        case 30:
        name = "Oita";
        break;
        case 31:
        name = "Okayama";
        break;
        case 32:
        name = "Osaka";
        break;
        case 33:
        name = "Saga";
        break;
        case 34:
        name = "Saitama";
        break;
        case 35:
        name = "Shiga";
        break;
        case 36:
        name = "Shimane";
        break;
        case 37:
        name = "Shizuoka";
        break;
        case 38:
        name = "Tochigi";
        break;
        case 39:
        name = "Tokushima";
        break;
        case 40:
        name = "Tokyo";
        break;
        case 41:
        name = "Tottori";
        break;
        case 42:
        name = "Toyama";
        break;
        case 43:
        name = "Wakayama";
        break;
        case 44:
        name = "Yamagata";
        break;
        case 45:
        name = "Yamaguchi";
        break;
        case 46:
        name = "Yamanashi";
        break;
        case 47:
        name = "Okinawa";
        break;
      }
    }
    if (country_code.equals("KE") == true) {
      switch (region_code2) {
        case 1:
        name = "Central";
        break;
        case 2:
        name = "Coast";
        break;
        case 3:
        name = "Eastern";
        break;
        case 5:
        name = "Nairobi Area";
        break;
        case 6:
        name = "North-Eastern";
        break;
        case 7:
        name = "Nyanza";
        break;
        case 8:
        name = "Rift Valley";
        break;
        case 9:
        name = "Western";
        break;
      }
    }
    if (country_code.equals("KG") == true) {
      switch (region_code2) {
        case 1:
        name = "Bishkek";
        break;
        case 2:
        name = "Chuy";
        break;
        case 3:
        name = "Jalal-Abad";
        break;
        case 4:
        name = "Naryn";
        break;
        case 5:
        name = "Osh";
        break;
        case 6:
        name = "Talas";
        break;
        case 7:
        name = "Ysyk-Kol";
        break;
        case 8:
        name = "Osh";
        break;
        case 9:
        name = "Batken";
        break;
      }
    }
    if (country_code.equals("KH") == true) {
      switch (region_code2) {
        case 1:
        name = "Batdambang";
        break;
        case 2:
        name = "Kampong Cham";
        break;
        case 3:
        name = "Kampong Chhnang";
        break;
        case 4:
        name = "Kampong Speu";
        break;
        case 5:
        name = "Kampong Thum";
        break;
        case 6:
        name = "Kampot";
        break;
        case 7:
        name = "Kandal";
        break;
        case 8:
        name = "Koh Kong";
        break;
        case 9:
        name = "Kracheh";
        break;
        case 10:
        name = "Mondulkiri";
        break;
        case 11:
        name = "Phnum Penh";
        break;
        case 12:
        name = "Pursat";
        break;
        case 13:
        name = "Preah Vihear";
        break;
        case 14:
        name = "Prey Veng";
        break;
        case 15:
        name = "Ratanakiri Kiri";
        break;
        case 16:
        name = "Siem Reap";
        break;
        case 17:
        name = "Stung Treng";
        break;
        case 18:
        name = "Svay Rieng";
        break;
        case 19:
        name = "Takeo";
        break;
        case 25:
        name = "Banteay Meanchey";
        break;
        case 29:
        name = "Batdambang";
        break;
        case 30:
        name = "Pailin";
        break;
      }
    }
    if (country_code.equals("KI") == true) {
      switch (region_code2) {
        case 1:
        name = "Gilbert Islands";
        break;
        case 2:
        name = "Line Islands";
        break;
        case 3:
        name = "Phoenix Islands";
        break;
      }
    }
    if (country_code.equals("KM") == true) {
      switch (region_code2) {
        case 1:
        name = "Anjouan";
        break;
        case 2:
        name = "Grande Comore";
        break;
        case 3:
        name = "Moheli";
        break;
      }
    }
    if (country_code.equals("KN") == true) {
      switch (region_code2) {
        case 1:
        name = "Christ Church Nichola Town";
        break;
        case 2:
        name = "Saint Anne Sandy Point";
        break;
        case 3:
        name = "Saint George Basseterre";
        break;
        case 4:
        name = "Saint George Gingerland";
        break;
        case 5:
        name = "Saint James Windward";
        break;
        case 6:
        name = "Saint John Capisterre";
        break;
        case 7:
        name = "Saint John Figtree";
        break;
        case 8:
        name = "Saint Mary Cayon";
        break;
        case 9:
        name = "Saint Paul Capisterre";
        break;
        case 10:
        name = "Saint Paul Charlestown";
        break;
        case 11:
        name = "Saint Peter Basseterre";
        break;
        case 12:
        name = "Saint Thomas Lowland";
        break;
        case 13:
        name = "Saint Thomas Middle Island";
        break;
        case 15:
        name = "Trinity Palmetto Point";
        break;
      }
    }
    if (country_code.equals("KP") == true) {
      switch (region_code2) {
        case 1:
        name = "Chagang-do";
        break;
        case 3:
        name = "Hamgyong-namdo";
        break;
        case 6:
        name = "Hwanghae-namdo";
        break;
        case 7:
        name = "Hwanghae-bukto";
        break;
        case 8:
        name = "Kaesong-si";
        break;
        case 9:
        name = "Kangwon-do";
        break;
        case 11:
        name = "P'yongan-bukto";
        break;
        case 12:
        name = "P'yongyang-si";
        break;
        case 13:
        name = "Yanggang-do";
        break;
        case 14:
        name = "Namp'o-si";
        break;
        case 15:
        name = "P'yongan-namdo";
        break;
        case 17:
        name = "Hamgyong-bukto";
        break;
        case 18:
        name = "Najin Sonbong-si";
        break;
      }
    }
    if (country_code.equals("KR") == true) {
      switch (region_code2) {
        case 1:
        name = "Cheju-do";
        break;
        case 3:
        name = "Cholla-bukto";
        break;
        case 5:
        name = "Ch'ungch'ong-bukto";
        break;
        case 6:
        name = "Kangwon-do";
        break;
        case 10:
        name = "Pusan-jikhalsi";
        break;
        case 11:
        name = "Seoul-t'ukpyolsi";
        break;
        case 12:
        name = "Inch'on-jikhalsi";
        break;
        case 13:
        name = "Kyonggi-do";
        break;
        case 14:
        name = "Kyongsang-bukto";
        break;
        case 15:
        name = "Taegu-jikhalsi";
        break;
        case 16:
        name = "Cholla-namdo";
        break;
        case 17:
        name = "Ch'ungch'ong-namdo";
        break;
        case 18:
        name = "Kwangju-jikhalsi";
        break;
        case 19:
        name = "Taejon-jikhalsi";
        break;
        case 20:
        name = "Kyongsang-namdo";
        break;
        case 21:
        name = "Ulsan-gwangyoksi";
        break;
      }
    }
    if (country_code.equals("KW") == true) {
      switch (region_code2) {
        case 1:
        name = "Al Ahmadi";
        break;
        case 2:
        name = "Al Kuwayt";
        break;
        case 5:
        name = "Al Jahra";
        break;
        case 7:
        name = "Al Farwaniyah";
        break;
        case 8:
        name = "Hawalli";
        break;
        case 9:
        name = "Mubarak al Kabir";
        break;
      }
    }
    if (country_code.equals("KY") == true) {
      switch (region_code2) {
        case 1:
        name = "Creek";
        break;
        case 2:
        name = "Eastern";
        break;
        case 3:
        name = "Midland";
        break;
        case 4:
        name = "South Town";
        break;
        case 5:
        name = "Spot Bay";
        break;
        case 6:
        name = "Stake Bay";
        break;
        case 7:
        name = "West End";
        break;
        case 8:
        name = "Western";
        break;
      }
    }
    if (country_code.equals("KZ") == true) {
      switch (region_code2) {
        case 1:
        name = "Almaty";
        break;
        case 2:
        name = "Almaty City";
        break;
        case 3:
        name = "Aqmola";
        break;
        case 4:
        name = "Aqtobe";
        break;
        case 5:
        name = "Astana";
        break;
        case 6:
        name = "Atyrau";
        break;
        case 7:
        name = "West Kazakhstan";
        break;
        case 8:
        name = "Bayqonyr";
        break;
        case 9:
        name = "Mangghystau";
        break;
        case 10:
        name = "South Kazakhstan";
        break;
        case 11:
        name = "Pavlodar";
        break;
        case 12:
        name = "Qaraghandy";
        break;
        case 13:
        name = "Qostanay";
        break;
        case 14:
        name = "Qyzylorda";
        break;
        case 15:
        name = "East Kazakhstan";
        break;
        case 16:
        name = "North Kazakhstan";
        break;
        case 17:
        name = "Zhambyl";
        break;
      }
    }
    if (country_code.equals("LA") == true) {
      switch (region_code2) {
        case 1:
        name = "Attapu";
        break;
        case 2:
        name = "Champasak";
        break;
        case 3:
        name = "Houaphan";
        break;
        case 4:
        name = "Khammouan";
        break;
        case 5:
        name = "Louang Namtha";
        break;
        case 7:
        name = "Oudomxai";
        break;
        case 8:
        name = "Phongsali";
        break;
        case 9:
        name = "Saravan";
        break;
        case 10:
        name = "Savannakhet";
        break;
        case 11:
        name = "Vientiane";
        break;
        case 13:
        name = "Xaignabouri";
        break;
        case 14:
        name = "Xiangkhoang";
        break;
        case 17:
        name = "Louangphrabang";
        break;
      }
    }
    if (country_code.equals("LB") == true) {
      switch (region_code2) {
        case 1:
        name = "Beqaa";
        break;
        case 2:
        name = "Al Janub";
        break;
        case 3:
        name = "Liban-Nord";
        break;
        case 4:
        name = "Beyrouth";
        break;
        case 5:
        name = "Mont-Liban";
        break;
        case 6:
        name = "Liban-Sud";
        break;
        case 7:
        name = "Nabatiye";
        break;
        case 8:
        name = "Beqaa";
        break;
        case 9:
        name = "Liban-Nord";
        break;
        case 10:
        name = "Aakk";
        break;
        case 11:
        name = "Baalbek-Hermel";
        break;
      }
    }
    if (country_code.equals("LC") == true) {
      switch (region_code2) {
        case 1:
        name = "Anse-la-Raye";
        break;
        case 2:
        name = "Dauphin";
        break;
        case 3:
        name = "Castries";
        break;
        case 4:
        name = "Choiseul";
        break;
        case 5:
        name = "Dennery";
        break;
        case 6:
        name = "Gros-Islet";
        break;
        case 7:
        name = "Laborie";
        break;
        case 8:
        name = "Micoud";
        break;
        case 9:
        name = "Soufriere";
        break;
        case 10:
        name = "Vieux-Fort";
        break;
        case 11:
        name = "Praslin";
        break;
      }
    }
    if (country_code.equals("LI") == true) {
      switch (region_code2) {
        case 1:
        name = "Balzers";
        break;
        case 2:
        name = "Eschen";
        break;
        case 3:
        name = "Gamprin";
        break;
        case 4:
        name = "Mauren";
        break;
        case 5:
        name = "Planken";
        break;
        case 6:
        name = "Ruggell";
        break;
        case 7:
        name = "Schaan";
        break;
        case 8:
        name = "Schellenberg";
        break;
        case 9:
        name = "Triesen";
        break;
        case 10:
        name = "Triesenberg";
        break;
        case 11:
        name = "Vaduz";
        break;
        case 21:
        name = "Gbarpolu";
        break;
        case 22:
        name = "River Gee";
        break;
      }
    }
    if (country_code.equals("LK") == true) {
      switch (region_code2) {
        case 1:
        name = "Amparai";
        break;
        case 2:
        name = "Anuradhapura";
        break;
        case 3:
        name = "Badulla";
        break;
        case 4:
        name = "Batticaloa";
        break;
        case 6:
        name = "Galle";
        break;
        case 7:
        name = "Hambantota";
        break;
        case 9:
        name = "Kalutara";
        break;
        case 10:
        name = "Kandy";
        break;
        case 11:
        name = "Kegalla";
        break;
        case 12:
        name = "Kurunegala";
        break;
        case 14:
        name = "Matale";
        break;
        case 15:
        name = "Matara";
        break;
        case 16:
        name = "Moneragala";
        break;
        case 17:
        name = "Nuwara Eliya";
        break;
        case 18:
        name = "Polonnaruwa";
        break;
        case 19:
        name = "Puttalam";
        break;
        case 20:
        name = "Ratnapura";
        break;
        case 21:
        name = "Trincomalee";
        break;
        case 23:
        name = "Colombo";
        break;
        case 24:
        name = "Gampaha";
        break;
        case 25:
        name = "Jaffna";
        break;
        case 26:
        name = "Mannar";
        break;
        case 27:
        name = "Mullaittivu";
        break;
        case 28:
        name = "Vavuniya";
        break;
        case 29:
        name = "Central";
        break;
        case 30:
        name = "North Central";
        break;
        case 31:
        name = "Northern";
        break;
        case 32:
        name = "North Western";
        break;
        case 33:
        name = "Sabaragamuwa";
        break;
        case 34:
        name = "Southern";
        break;
        case 35:
        name = "Uva";
        break;
        case 36:
        name = "Western";
        break;
      }
    }
    if (country_code.equals("LR") == true) {
      switch (region_code2) {
        case 1:
        name = "Bong";
        break;
        case 4:
        name = "Grand Cape Mount";
        break;
        case 5:
        name = "Lofa";
        break;
        case 6:
        name = "Maryland";
        break;
        case 7:
        name = "Monrovia";
        break;
        case 9:
        name = "Nimba";
        break;
        case 10:
        name = "Sino";
        break;
        case 11:
        name = "Grand Bassa";
        break;
        case 12:
        name = "Grand Cape Mount";
        break;
        case 13:
        name = "Maryland";
        break;
        case 14:
        name = "Montserrado";
        break;
        case 17:
        name = "Margibi";
        break;
        case 18:
        name = "River Cess";
        break;
        case 19:
        name = "Grand Gedeh";
        break;
        case 20:
        name = "Lofa";
        break;
        case 21:
        name = "Gbarpolu";
        break;
        case 22:
        name = "River Gee";
        break;
      }
    }
    if (country_code.equals("LS") == true) {
      switch (region_code2) {
        case 10:
        name = "Berea";
        break;
        case 11:
        name = "Butha-Buthe";
        break;
        case 12:
        name = "Leribe";
        break;
        case 13:
        name = "Mafeteng";
        break;
        case 14:
        name = "Maseru";
        break;
        case 15:
        name = "Mohales Hoek";
        break;
        case 16:
        name = "Mokhotlong";
        break;
        case 17:
        name = "Qachas Nek";
        break;
        case 18:
        name = "Quthing";
        break;
        case 19:
        name = "Thaba-Tseka";
        break;
      }
    }
    if (country_code.equals("LT") == true) {
      switch (region_code2) {
        case 56:
        name = "Alytaus Apskritis";
        break;
        case 57:
        name = "Kauno Apskritis";
        break;
        case 58:
        name = "Klaipedos Apskritis";
        break;
        case 59:
        name = "Marijampoles Apskritis";
        break;
        case 60:
        name = "Panevezio Apskritis";
        break;
        case 61:
        name = "Siauliu Apskritis";
        break;
        case 62:
        name = "Taurages Apskritis";
        break;
        case 63:
        name = "Telsiu Apskritis";
        break;
        case 64:
        name = "Utenos Apskritis";
        break;
        case 65:
        name = "Vilniaus Apskritis";
        break;
      }
    }
    if (country_code.equals("LU") == true) {
      switch (region_code2) {
        case 1:
        name = "Diekirch";
        break;
        case 2:
        name = "Grevenmacher";
        break;
        case 3:
        name = "Luxembourg";
        break;
      }
    }
    if (country_code.equals("LV") == true) {
      switch (region_code2) {
        case 1:
        name = "Aizkraukles";
        break;
        case 2:
        name = "Aluksnes";
        break;
        case 3:
        name = "Balvu";
        break;
        case 4:
        name = "Bauskas";
        break;
        case 5:
        name = "Cesu";
        break;
        case 6:
        name = "Daugavpils";
        break;
        case 7:
        name = "Daugavpils";
        break;
        case 8:
        name = "Dobeles";
        break;
        case 9:
        name = "Gulbenes";
        break;
        case 10:
        name = "Jekabpils";
        break;
        case 11:
        name = "Jelgava";
        break;
        case 12:
        name = "Jelgavas";
        break;
        case 13:
        name = "Jurmala";
        break;
        case 14:
        name = "Kraslavas";
        break;
        case 15:
        name = "Kuldigas";
        break;
        case 16:
        name = "Liepaja";
        break;
        case 17:
        name = "Liepajas";
        break;
        case 18:
        name = "Limbazu";
        break;
        case 19:
        name = "Ludzas";
        break;
        case 20:
        name = "Madonas";
        break;
        case 21:
        name = "Ogres";
        break;
        case 22:
        name = "Preilu";
        break;
        case 23:
        name = "Rezekne";
        break;
        case 24:
        name = "Rezeknes";
        break;
        case 25:
        name = "Riga";
        break;
        case 26:
        name = "Rigas";
        break;
        case 27:
        name = "Saldus";
        break;
        case 28:
        name = "Talsu";
        break;
        case 29:
        name = "Tukuma";
        break;
        case 30:
        name = "Valkas";
        break;
        case 31:
        name = "Valmieras";
        break;
        case 32:
        name = "Ventspils";
        break;
        case 33:
        name = "Ventspils";
        break;
      }
    }
    if (country_code.equals("LY") == true) {
      switch (region_code2) {
        case 3:
        name = "Al Aziziyah";
        break;
        case 5:
        name = "Al Jufrah";
        break;
        case 8:
        name = "Al Kufrah";
        break;
        case 13:
        name = "Ash Shati'";
        break;
        case 30:
        name = "Murzuq";
        break;
        case 34:
        name = "Sabha";
        break;
        case 41:
        name = "Tarhunah";
        break;
        case 42:
        name = "Tubruq";
        break;
        case 45:
        name = "Zlitan";
        break;
        case 47:
        name = "Ajdabiya";
        break;
        case 48:
        name = "Al Fatih";
        break;
        case 49:
        name = "Al Jabal al Akhdar";
        break;
        case 50:
        name = "Al Khums";
        break;
        case 51:
        name = "An Nuqat al Khams";
        break;
        case 52:
        name = "Awbari";
        break;
        case 53:
        name = "Az Zawiyah";
        break;
        case 54:
        name = "Banghazi";
        break;
        case 55:
        name = "Darnah";
        break;
        case 56:
        name = "Ghadamis";
        break;
        case 57:
        name = "Gharyan";
        break;
        case 58:
        name = "Misratah";
        break;
        case 59:
        name = "Sawfajjin";
        break;
        case 60:
        name = "Surt";
        break;
        case 61:
        name = "Tarabulus";
        break;
        case 62:
        name = "Yafran";
        break;
      }
    }
    if (country_code.equals("MA") == true) {
      switch (region_code2) {
        case 1:
        name = "Agadir";
        break;
        case 2:
        name = "Al Hoceima";
        break;
        case 3:
        name = "Azilal";
        break;
        case 4:
        name = "Ben Slimane";
        break;
        case 5:
        name = "Beni Mellal";
        break;
        case 6:
        name = "Boulemane";
        break;
        case 7:
        name = "Casablanca";
        break;
        case 8:
        name = "Chaouen";
        break;
        case 9:
        name = "El Jadida";
        break;
        case 10:
        name = "El Kelaa des Srarhna";
        break;
        case 11:
        name = "Er Rachidia";
        break;
        case 12:
        name = "Essaouira";
        break;
        case 13:
        name = "Fes";
        break;
        case 14:
        name = "Figuig";
        break;
        case 15:
        name = "Kenitra";
        break;
        case 16:
        name = "Khemisset";
        break;
        case 17:
        name = "Khenifra";
        break;
        case 18:
        name = "Khouribga";
        break;
        case 19:
        name = "Marrakech";
        break;
        case 20:
        name = "Meknes";
        break;
        case 21:
        name = "Nador";
        break;
        case 22:
        name = "Ouarzazate";
        break;
        case 23:
        name = "Oujda";
        break;
        case 24:
        name = "Rabat-Sale";
        break;
        case 25:
        name = "Safi";
        break;
        case 26:
        name = "Settat";
        break;
        case 27:
        name = "Tanger";
        break;
        case 29:
        name = "Tata";
        break;
        case 30:
        name = "Taza";
        break;
        case 32:
        name = "Tiznit";
        break;
        case 33:
        name = "Guelmim";
        break;
        case 34:
        name = "Ifrane";
        break;
        case 35:
        name = "Laayoune";
        break;
        case 36:
        name = "Tan-Tan";
        break;
        case 37:
        name = "Taounate";
        break;
        case 38:
        name = "Sidi Kacem";
        break;
        case 39:
        name = "Taroudannt";
        break;
        case 40:
        name = "Tetouan";
        break;
        case 41:
        name = "Larache";
        break;
        case 45:
        name = "Grand Casablanca";
        break;
        case 46:
        name = "Fes-Boulemane";
        break;
        case 47:
        name = "Marrakech-Tensift-Al Haouz";
        break;
        case 48:
        name = "Meknes-Tafilalet";
        break;
        case 49:
        name = "Rabat-Sale-Zemmour-Zaer";
        break;
        case 50:
        name = "Chaouia-Ouardigha";
        break;
        case 51:
        name = "Doukkala-Abda";
        break;
        case 52:
        name = "Gharb-Chrarda-Beni Hssen";
        break;
        case 53:
        name = "Guelmim-Es Smara";
        break;
        case 54:
        name = "Oriental";
        break;
        case 55:
        name = "Souss-Massa-Dr";
        break;
        case 56:
        name = "Tadla-Azilal";
        break;
        case 57:
        name = "Tanger-Tetouan";
        break;
        case 58:
        name = "Taza-Al Hoceima-Taounate";
        break;
        case 59:
        name = "La";
        break;
      }
    }
    if (country_code.equals("MC") == true) {
      switch (region_code2) {
        case 1:
        name = "La Condamine";
        break;
        case 2:
        name = "Monaco";
        break;
        case 3:
        name = "Monte-Carlo";
        break;
      }
    }
    if (country_code.equals("MD") == true) {
      switch (region_code2) {
        case 46:
        name = "Balti";
        break;
        case 47:
        name = "Cahul";
        break;
        case 48:
        name = "Chisinau";
        break;
        case 49:
        name = "Stinga Nistrului";
        break;
        case 50:
        name = "Edinet";
        break;
        case 51:
        name = "Gagauzia";
        break;
        case 52:
        name = "Lapusna";
        break;
        case 53:
        name = "Orhei";
        break;
        case 54:
        name = "Soroca";
        break;
        case 55:
        name = "Tighina";
        break;
        case 56:
        name = "Ungheni";
        break;
        case 58:
        name = "Stinga Nistrului";
        break;
        case 59:
        name = "Anenii Noi";
        break;
        case 60:
        name = "Balti";
        break;
        case 61:
        name = "Basarabeasca";
        break;
        case 62:
        name = "Bender";
        break;
        case 63:
        name = "Briceni";
        break;
        case 64:
        name = "Cahul";
        break;
        case 65:
        name = "Cantemir";
        break;
        case 66:
        name = "Calarasi";
        break;
        case 67:
        name = "Causeni";
        break;
        case 68:
        name = "Cimislia";
        break;
        case 69:
        name = "Criuleni";
        break;
        case 70:
        name = "Donduseni";
        break;
        case 71:
        name = "Drochia";
        break;
        case 72:
        name = "Dubasari";
        break;
        case 73:
        name = "Edinet";
        break;
        case 74:
        name = "Falesti";
        break;
        case 75:
        name = "Floresti";
        break;
        case 76:
        name = "Glodeni";
        break;
        case 77:
        name = "Hincesti";
        break;
        case 78:
        name = "Ialoveni";
        break;
        case 79:
        name = "Leova";
        break;
        case 80:
        name = "Nisporeni";
        break;
        case 81:
        name = "Ocnita";
        break;
        case 83:
        name = "Rezina";
        break;
        case 84:
        name = "Riscani";
        break;
        case 85:
        name = "Singerei";
        break;
        case 86:
        name = "Soldanesti";
        break;
        case 87:
        name = "Soroca";
        break;
        case 88:
        name = "Stefan-Voda";
        break;
        case 89:
        name = "Straseni";
        break;
        case 90:
        name = "Taraclia";
        break;
        case 91:
        name = "Telenesti";
        break;
        case 92:
        name = "Ungheni";
        break;
      }
    }
    if (country_code.equals("MG") == true) {
      switch (region_code2) {
        case 1:
        name = "Antsiranana";
        break;
        case 2:
        name = "Fianarantsoa";
        break;
        case 3:
        name = "Mahajanga";
        break;
        case 4:
        name = "Toamasina";
        break;
        case 5:
        name = "Antananarivo";
        break;
        case 6:
        name = "Toliara";
        break;
      }
    }
    if (country_code.equals("MK") == true) {
      switch (region_code2) {
        case 1:
        name = "Aracinovo";
        break;
        case 2:
        name = "Bac";
        break;
        case 3:
        name = "Belcista";
        break;
        case 4:
        name = "Berovo";
        break;
        case 5:
        name = "Bistrica";
        break;
        case 6:
        name = "Bitola";
        break;
        case 7:
        name = "Blatec";
        break;
        case 8:
        name = "Bogdanci";
        break;
        case 9:
        name = "Bogomila";
        break;
        case 10:
        name = "Bogovinje";
        break;
        case 11:
        name = "Bosilovo";
        break;
        case 12:
        name = "Brvenica";
        break;
        case 13:
        name = "Cair";
        break;
        case 14:
        name = "Capari";
        break;
        case 15:
        name = "Caska";
        break;
        case 16:
        name = "Cegrane";
        break;
        case 17:
        name = "Centar";
        break;
        case 18:
        name = "Centar Zupa";
        break;
        case 19:
        name = "Cesinovo";
        break;
        case 20:
        name = "Cucer-Sandevo";
        break;
        case 21:
        name = "Debar";
        break;
        case 22:
        name = "Delcevo";
        break;
        case 23:
        name = "Delogozdi";
        break;
        case 24:
        name = "Demir Hisar";
        break;
        case 25:
        name = "Demir Kapija";
        break;
        case 26:
        name = "Dobrusevo";
        break;
        case 27:
        name = "Dolna Banjica";
        break;
        case 28:
        name = "Dolneni";
        break;
        case 29:
        name = "Dorce Petrov";
        break;
        case 30:
        name = "Drugovo";
        break;
        case 31:
        name = "Dzepciste";
        break;
        case 32:
        name = "Gazi Baba";
        break;
        case 33:
        name = "Gevgelija";
        break;
        case 34:
        name = "Gostivar";
        break;
        case 35:
        name = "Gradsko";
        break;
        case 36:
        name = "Ilinden";
        break;
        case 37:
        name = "Izvor";
        break;
        case 38:
        name = "Jegunovce";
        break;
        case 39:
        name = "Kamenjane";
        break;
        case 40:
        name = "Karbinci";
        break;
        case 41:
        name = "Karpos";
        break;
        case 42:
        name = "Kavadarci";
        break;
        case 43:
        name = "Kicevo";
        break;
        case 44:
        name = "Kisela Voda";
        break;
        case 45:
        name = "Klecevce";
        break;
        case 46:
        name = "Kocani";
        break;
        case 47:
        name = "Konce";
        break;
        case 48:
        name = "Kondovo";
        break;
        case 49:
        name = "Konopiste";
        break;
        case 50:
        name = "Kosel";
        break;
        case 51:
        name = "Kratovo";
        break;
        case 52:
        name = "Kriva Palanka";
        break;
        case 53:
        name = "Krivogastani";
        break;
        case 54:
        name = "Krusevo";
        break;
        case 55:
        name = "Kuklis";
        break;
        case 56:
        name = "Kukurecani";
        break;
        case 57:
        name = "Kumanovo";
        break;
        case 58:
        name = "Labunista";
        break;
        case 59:
        name = "Lipkovo";
        break;
        case 60:
        name = "Lozovo";
        break;
        case 61:
        name = "Lukovo";
        break;
        case 62:
        name = "Makedonska Kamenica";
        break;
        case 63:
        name = "Makedonski Brod";
        break;
        case 64:
        name = "Mavrovi Anovi";
        break;
        case 65:
        name = "Meseista";
        break;
        case 66:
        name = "Miravci";
        break;
        case 67:
        name = "Mogila";
        break;
        case 68:
        name = "Murtino";
        break;
        case 69:
        name = "Negotino";
        break;
        case 70:
        name = "Negotino-Polosko";
        break;
        case 71:
        name = "Novaci";
        break;
        case 72:
        name = "Novo Selo";
        break;
        case 73:
        name = "Oblesevo";
        break;
        case 74:
        name = "Ohrid";
        break;
        case 75:
        name = "Orasac";
        break;
        case 76:
        name = "Orizari";
        break;
        case 77:
        name = "Oslomej";
        break;
        case 78:
        name = "Pehcevo";
        break;
        case 79:
        name = "Petrovec";
        break;
        case 80:
        name = "Plasnica";
        break;
        case 81:
        name = "Podares";
        break;
        case 82:
        name = "Prilep";
        break;
        case 83:
        name = "Probistip";
        break;
        case 84:
        name = "Radovis";
        break;
        case 85:
        name = "Rankovce";
        break;
        case 86:
        name = "Resen";
        break;
        case 87:
        name = "Rosoman";
        break;
        case 88:
        name = "Rostusa";
        break;
        case 89:
        name = "Samokov";
        break;
        case 90:
        name = "Saraj";
        break;
        case 91:
        name = "Sipkovica";
        break;
        case 92:
        name = "Sopiste";
        break;
        case 93:
        name = "Sopotnica";
        break;
        case 94:
        name = "Srbinovo";
        break;
        case 95:
        name = "Staravina";
        break;
        case 96:
        name = "Star Dojran";
        break;
        case 97:
        name = "Staro Nagoricane";
        break;
        case 98:
        name = "Stip";
        break;
        case 99:
        name = "Struga";
        break;
        case 832:
        name = "Strumica";
        break;
        case 833:
        name = "Studenicani";
        break;
        case 834:
        name = "Suto Orizari";
        break;
        case 835:
        name = "Sveti Nikole";
        break;
        case 836:
        name = "Tearce";
        break;
        case 837:
        name = "Tetovo";
        break;
        case 838:
        name = "Topolcani";
        break;
        case 839:
        name = "Valandovo";
        break;
        case 840:
        name = "Vasilevo";
        break;
        case 875:
        name = "Veles";
        break;
        case 876:
        name = "Velesta";
        break;
        case 877:
        name = "Vevcani";
        break;
        case 878:
        name = "Vinica";
        break;
        case 879:
        name = "Vitoliste";
        break;
        case 880:
        name = "Vranestica";
        break;
        case 881:
        name = "Vrapciste";
        break;
        case 882:
        name = "Vratnica";
        break;
        case 883:
        name = "Vrutok";
        break;
        case 918:
        name = "Zajas";
        break;
        case 919:
        name = "Zelenikovo";
        break;
        case 920:
        name = "Zelino";
        break;
        case 921:
        name = "Zitose";
        break;
        case 922:
        name = "Zletovo";
        break;
        case 923:
        name = "Zrnovci";
        break;
      }
    }
    if (country_code.equals("ML") == true) {
      switch (region_code2) {
        case 1:
        name = "Bamako";
        break;
        case 3:
        name = "Kayes";
        break;
        case 4:
        name = "Mopti";
        break;
        case 5:
        name = "Segou";
        break;
        case 6:
        name = "Sikasso";
        break;
        case 7:
        name = "Koulikoro";
        break;
        case 8:
        name = "Tombouctou";
        break;
        case 9:
        name = "Gao";
        break;
        case 10:
        name = "Kidal";
        break;
      }
    }
    if (country_code.equals("MM") == true) {
      switch (region_code2) {
        case 1:
        name = "Rakhine State";
        break;
        case 2:
        name = "Chin State";
        break;
        case 3:
        name = "Irrawaddy";
        break;
        case 4:
        name = "Kachin State";
        break;
        case 5:
        name = "Karan State";
        break;
        case 6:
        name = "Kayah State";
        break;
        case 7:
        name = "Magwe";
        break;
        case 8:
        name = "Mandalay";
        break;
        case 9:
        name = "Pegu";
        break;
        case 10:
        name = "Sagaing";
        break;
        case 11:
        name = "Shan State";
        break;
        case 12:
        name = "Tenasserim";
        break;
        case 13:
        name = "Mon State";
        break;
        case 14:
        name = "Rangoon";
        break;
        case 17:
        name = "Yangon";
        break;
      }
    }
    if (country_code.equals("MN") == true) {
      switch (region_code2) {
        case 1:
        name = "Arhangay";
        break;
        case 2:
        name = "Bayanhongor";
        break;
        case 3:
        name = "Bayan-Olgiy";
        break;
        case 5:
        name = "Darhan";
        break;
        case 6:
        name = "Dornod";
        break;
        case 7:
        name = "Dornogovi";
        break;
        case 8:
        name = "Dundgovi";
        break;
        case 9:
        name = "Dzavhan";
        break;
        case 10:
        name = "Govi-Altay";
        break;
        case 11:
        name = "Hentiy";
        break;
        case 12:
        name = "Hovd";
        break;
        case 13:
        name = "Hovsgol";
        break;
        case 14:
        name = "Omnogovi";
        break;
        case 15:
        name = "Ovorhangay";
        break;
        case 16:
        name = "Selenge";
        break;
        case 17:
        name = "Suhbaatar";
        break;
        case 18:
        name = "Tov";
        break;
        case 19:
        name = "Uvs";
        break;
        case 20:
        name = "Ulaanbaatar";
        break;
        case 21:
        name = "Bulgan";
        break;
        case 22:
        name = "Erdenet";
        break;
        case 23:
        name = "Darhan-Uul";
        break;
        case 24:
        name = "Govisumber";
        break;
        case 25:
        name = "Orhon";
        break;
      }
    }
    if (country_code.equals("MO") == true) {
      switch (region_code2) {
        case 1:
        name = "Ilhas";
        break;
        case 2:
        name = "Macau";
        break;
      }
    }
    if (country_code.equals("MR") == true) {
      switch (region_code2) {
        case 1:
        name = "Hodh Ech Chargui";
        break;
        case 2:
        name = "Hodh El Gharbi";
        break;
        case 3:
        name = "Assaba";
        break;
        case 4:
        name = "Gorgol";
        break;
        case 5:
        name = "Brakna";
        break;
        case 6:
        name = "Trarza";
        break;
        case 7:
        name = "Adrar";
        break;
        case 8:
        name = "Dakhlet Nouadhibou";
        break;
        case 9:
        name = "Tagant";
        break;
        case 10:
        name = "Guidimaka";
        break;
        case 11:
        name = "Tiris Zemmour";
        break;
        case 12:
        name = "Inchiri";
        break;
      }
    }
    if (country_code.equals("MS") == true) {
      switch (region_code2) {
        case 1:
        name = "Saint Anthony";
        break;
        case 2:
        name = "Saint Georges";
        break;
        case 3:
        name = "Saint Peter";
        break;
      }
    }
    if (country_code.equals("MU") == true) {
      switch (region_code2) {
        case 12:
        name = "Black River";
        break;
        case 13:
        name = "Flacq";
        break;
        case 14:
        name = "Grand Port";
        break;
        case 15:
        name = "Moka";
        break;
        case 16:
        name = "Pamplemousses";
        break;
        case 17:
        name = "Plaines Wilhems";
        break;
        case 18:
        name = "Port Louis";
        break;
        case 19:
        name = "Riviere du Rempart";
        break;
        case 20:
        name = "Savanne";
        break;
        case 21:
        name = "Agalega Islands";
        break;
        case 22:
        name = "Cargados Carajos";
        break;
        case 23:
        name = "Rodrigues";
        break;
      }
    }
    if (country_code.equals("MV") == true) {
      switch (region_code2) {
        case 1:
        name = "Seenu";
        break;
        case 2:
        name = "Aliff";
        break;
        case 3:
        name = "Laviyani";
        break;
        case 4:
        name = "Waavu";
        break;
        case 5:
        name = "Laamu";
        break;
        case 7:
        name = "Haa Aliff";
        break;
        case 8:
        name = "Thaa";
        break;
        case 12:
        name = "Meemu";
        break;
        case 13:
        name = "Raa";
        break;
        case 14:
        name = "Faafu";
        break;
        case 17:
        name = "Daalu";
        break;
        case 20:
        name = "Baa";
        break;
        case 23:
        name = "Haa Daalu";
        break;
        case 24:
        name = "Shaviyani";
        break;
        case 25:
        name = "Noonu";
        break;
        case 26:
        name = "Kaafu";
        break;
        case 27:
        name = "Gaafu Aliff";
        break;
        case 28:
        name = "Gaafu Daalu";
        break;
        case 29:
        name = "Naviyani";
        break;
        case 40:
        name = "Male";
        break;
      }
    }
    if (country_code.equals("MW") == true) {
      switch (region_code2) {
        case 2:
        name = "Chikwawa";
        break;
        case 3:
        name = "Chiradzulu";
        break;
        case 4:
        name = "Chitipa";
        break;
        case 5:
        name = "Thyolo";
        break;
        case 6:
        name = "Dedza";
        break;
        case 7:
        name = "Dowa";
        break;
        case 8:
        name = "Karonga";
        break;
        case 9:
        name = "Kasungu";
        break;
        case 11:
        name = "Lilongwe";
        break;
        case 12:
        name = "Mangochi";
        break;
        case 13:
        name = "Mchinji";
        break;
        case 15:
        name = "Mzimba";
        break;
        case 16:
        name = "Ntcheu";
        break;
        case 17:
        name = "Nkhata Bay";
        break;
        case 18:
        name = "Nkhotakota";
        break;
        case 19:
        name = "Nsanje";
        break;
        case 20:
        name = "Ntchisi";
        break;
        case 21:
        name = "Rumphi";
        break;
        case 22:
        name = "Salima";
        break;
        case 23:
        name = "Zomba";
        break;
        case 24:
        name = "Blantyre";
        break;
        case 25:
        name = "Mwanza";
        break;
        case 26:
        name = "Balaka";
        break;
        case 27:
        name = "Likoma";
        break;
        case 28:
        name = "Machinga";
        break;
        case 29:
        name = "Mulanje";
        break;
        case 30:
        name = "Phalombe";
        break;
      }
    }
    if (country_code.equals("MX") == true) {
      switch (region_code2) {
        case 1:
        name = "Aguascalientes";
        break;
        case 2:
        name = "Baja California";
        break;
        case 3:
        name = "Baja California Sur";
        break;
        case 4:
        name = "Campeche";
        break;
        case 5:
        name = "Chiapas";
        break;
        case 6:
        name = "Chihuahua";
        break;
        case 7:
        name = "Coahuila de Zaragoza";
        break;
        case 8:
        name = "Colima";
        break;
        case 9:
        name = "Distrito Federal";
        break;
        case 10:
        name = "Durango";
        break;
        case 11:
        name = "Guanajuato";
        break;
        case 12:
        name = "Guerrero";
        break;
        case 13:
        name = "Hidalgo";
        break;
        case 14:
        name = "Jalisco";
        break;
        case 15:
        name = "Mexico";
        break;
        case 16:
        name = "Michoacan de Ocampo";
        break;
        case 17:
        name = "Morelos";
        break;
        case 18:
        name = "Nayarit";
        break;
        case 19:
        name = "Nuevo Leon";
        break;
        case 20:
        name = "Oaxaca";
        break;
        case 21:
        name = "Puebla";
        break;
        case 22:
        name = "Queretaro de Arteaga";
        break;
        case 23:
        name = "Quintana Roo";
        break;
        case 24:
        name = "San Luis Potosi";
        break;
        case 25:
        name = "Sinaloa";
        break;
        case 26:
        name = "Sonora";
        break;
        case 27:
        name = "Tabasco";
        break;
        case 28:
        name = "Tamaulipas";
        break;
        case 29:
        name = "Tlaxcala";
        break;
        case 30:
        name = "Veracruz-Llave";
        break;
        case 31:
        name = "Yucatan";
        break;
        case 32:
        name = "Zacatecas";
        break;
      }
    }
    if (country_code.equals("MY") == true) {
      switch (region_code2) {
        case 1:
        name = "Johor";
        break;
        case 2:
        name = "Kedah";
        break;
        case 3:
        name = "Kelantan";
        break;
        case 4:
        name = "Melaka";
        break;
        case 5:
        name = "Negeri Sembilan";
        break;
        case 6:
        name = "Pahang";
        break;
        case 7:
        name = "Perak";
        break;
        case 8:
        name = "Perlis";
        break;
        case 9:
        name = "Pulau Pinang";
        break;
        case 11:
        name = "Sarawak";
        break;
        case 12:
        name = "Selangor";
        break;
        case 13:
        name = "Terengganu";
        break;
        case 14:
        name = "Kuala Lumpur";
        break;
        case 15:
        name = "Labuan";
        break;
        case 16:
        name = "Sabah";
        break;
        case 17:
        name = "Putrajaya";
        break;
      }
    }
    if (country_code.equals("MZ") == true) {
      switch (region_code2) {
        case 1:
        name = "Cabo Delgado";
        break;
        case 2:
        name = "Gaza";
        break;
        case 3:
        name = "Inhambane";
        break;
        case 4:
        name = "Maputo";
        break;
        case 5:
        name = "Sofala";
        break;
        case 6:
        name = "Nampula";
        break;
        case 7:
        name = "Niassa";
        break;
        case 8:
        name = "Tete";
        break;
        case 9:
        name = "Zambezia";
        break;
        case 10:
        name = "Manica";
        break;
        case 11:
        name = "Maputo";
        break;
      }
    }
    if (country_code.equals("NA") == true) {
      switch (region_code2) {
        case 1:
        name = "Bethanien";
        break;
        case 2:
        name = "Caprivi Oos";
        break;
        case 3:
        name = "Boesmanland";
        break;
        case 4:
        name = "Gobabis";
        break;
        case 5:
        name = "Grootfontein";
        break;
        case 6:
        name = "Kaokoland";
        break;
        case 7:
        name = "Karibib";
        break;
        case 8:
        name = "Keetmanshoop";
        break;
        case 9:
        name = "Luderitz";
        break;
        case 10:
        name = "Maltahohe";
        break;
        case 11:
        name = "Okahandja";
        break;
        case 12:
        name = "Omaruru";
        break;
        case 13:
        name = "Otjiwarongo";
        break;
        case 14:
        name = "Outjo";
        break;
        case 15:
        name = "Owambo";
        break;
        case 16:
        name = "Rehoboth";
        break;
        case 17:
        name = "Swakopmund";
        break;
        case 18:
        name = "Tsumeb";
        break;
        case 20:
        name = "Karasburg";
        break;
        case 21:
        name = "Windhoek";
        break;
        case 22:
        name = "Damaraland";
        break;
        case 23:
        name = "Hereroland Oos";
        break;
        case 24:
        name = "Hereroland Wes";
        break;
        case 25:
        name = "Kavango";
        break;
        case 26:
        name = "Mariental";
        break;
        case 27:
        name = "Namaland";
        break;
        case 28:
        name = "Caprivi";
        break;
        case 29:
        name = "Erongo";
        break;
        case 30:
        name = "Hardap";
        break;
        case 31:
        name = "Karas";
        break;
        case 32:
        name = "Kunene";
        break;
        case 33:
        name = "Ohangwena";
        break;
        case 34:
        name = "Okavango";
        break;
        case 35:
        name = "Omaheke";
        break;
        case 36:
        name = "Omusati";
        break;
        case 37:
        name = "Oshana";
        break;
        case 38:
        name = "Oshikoto";
        break;
        case 39:
        name = "Otjozondjupa";
        break;
      }
    }
    if (country_code.equals("NE") == true) {
      switch (region_code2) {
        case 1:
        name = "Agadez";
        break;
        case 2:
        name = "Diffa";
        break;
        case 3:
        name = "Dosso";
        break;
        case 4:
        name = "Maradi";
        break;
        case 5:
        name = "Niamey";
        break;
        case 6:
        name = "Tahoua";
        break;
        case 7:
        name = "Zinder";
        break;
        case 8:
        name = "Niamey";
        break;
      }
    }
    if (country_code.equals("NG") == true) {
      switch (region_code2) {
        case 5:
        name = "Lagos";
        break;
        case 10:
        name = "Rivers";
        break;
        case 11:
        name = "Federal Capital Territory";
        break;
        case 12:
        name = "Gongola";
        break;
        case 16:
        name = "Ogun";
        break;
        case 17:
        name = "Ondo";
        break;
        case 18:
        name = "Oyo";
        break;
        case 21:
        name = "Akwa Ibom";
        break;
        case 22:
        name = "Cross River";
        break;
        case 23:
        name = "Kaduna";
        break;
        case 24:
        name = "Katsina";
        break;
        case 25:
        name = "Anambra";
        break;
        case 26:
        name = "Benue";
        break;
        case 27:
        name = "Borno";
        break;
        case 28:
        name = "Imo";
        break;
        case 29:
        name = "Kano";
        break;
        case 30:
        name = "Kwara";
        break;
        case 31:
        name = "Niger";
        break;
        case 32:
        name = "Oyo";
        break;
        case 35:
        name = "Adamawa";
        break;
        case 36:
        name = "Delta";
        break;
        case 37:
        name = "Edo";
        break;
        case 39:
        name = "Jigawa";
        break;
        case 40:
        name = "Kebbi";
        break;
        case 41:
        name = "Kogi";
        break;
        case 42:
        name = "Osun";
        break;
        case 43:
        name = "Taraba";
        break;
        case 44:
        name = "Yobe";
        break;
        case 45:
        name = "Abia";
        break;
        case 46:
        name = "Bauchi";
        break;
        case 47:
        name = "Enugu";
        break;
        case 48:
        name = "Ondo";
        break;
        case 49:
        name = "Plateau";
        break;
        case 50:
        name = "Rivers";
        break;
        case 51:
        name = "Sokoto";
        break;
        case 52:
        name = "Bayelsa";
        break;
        case 53:
        name = "Ebonyi";
        break;
        case 54:
        name = "Ekiti";
        break;
        case 55:
        name = "Gombe";
        break;
        case 56:
        name = "Nassarawa";
        break;
        case 57:
        name = "Zamfara";
        break;
      }
    }
    if (country_code.equals("NI") == true) {
      switch (region_code2) {
        case 1:
        name = "Boaco";
        break;
        case 2:
        name = "Carazo";
        break;
        case 3:
        name = "Chinandega";
        break;
        case 4:
        name = "Chontales";
        break;
        case 5:
        name = "Esteli";
        break;
        case 6:
        name = "Granada";
        break;
        case 7:
        name = "Jinotega";
        break;
        case 8:
        name = "Leon";
        break;
        case 9:
        name = "Madriz";
        break;
        case 10:
        name = "Managua";
        break;
        case 11:
        name = "Masaya";
        break;
        case 12:
        name = "Matagalpa";
        break;
        case 13:
        name = "Nueva Segovia";
        break;
        case 14:
        name = "Rio San Juan";
        break;
        case 15:
        name = "Rivas";
        break;
        case 16:
        name = "Zelaya";
        break;
        case 17:
        name = "Autonoma Atlantico Norte";
        break;
        case 18:
        name = "Region Autonoma Atlantico Sur";
        break;
      }
    }
    if (country_code.equals("NL") == true) {
      switch (region_code2) {
        case 1:
        name = "Drenthe";
        break;
        case 2:
        name = "Friesland";
        break;
        case 3:
        name = "Gelderland";
        break;
        case 4:
        name = "Groningen";
        break;
        case 5:
        name = "Limburg";
        break;
        case 6:
        name = "Noord-Brabant";
        break;
        case 7:
        name = "Noord-Holland";
        break;
        case 8:
        name = "Overijssel";
        break;
        case 9:
        name = "Utrecht";
        break;
        case 10:
        name = "Zeeland";
        break;
        case 11:
        name = "Zuid-Holland";
        break;
        case 12:
        name = "Dronten";
        break;
        case 13:
        name = "Zuidelijke IJsselmeerpolders";
        break;
        case 14:
        name = "Lelystad";
        break;
        case 15:
        name = "Overijssel";
        break;
        case 16:
        name = "Flevoland";
        break;
      }
    }
    if (country_code.equals("NO") == true) {
      switch (region_code2) {
        case 1:
        name = "Akershus";
        break;
        case 2:
        name = "Aust-Agder";
        break;
        case 4:
        name = "Buskerud";
        break;
        case 5:
        name = "Finnmark";
        break;
        case 6:
        name = "Hedmark";
        break;
        case 7:
        name = "Hordaland";
        break;
        case 8:
        name = "More og Romsdal";
        break;
        case 9:
        name = "Nordland";
        break;
        case 10:
        name = "Nord-Trondelag";
        break;
        case 11:
        name = "Oppland";
        break;
        case 12:
        name = "Oslo";
        break;
        case 13:
        name = "Ostfold";
        break;
        case 14:
        name = "Rogaland";
        break;
        case 15:
        name = "Sogn og Fjordane";
        break;
        case 16:
        name = "Sor-Trondelag";
        break;
        case 17:
        name = "Telemark";
        break;
        case 18:
        name = "Troms";
        break;
        case 19:
        name = "Vest-Agder";
        break;
        case 20:
        name = "Vestfold";
        break;
      }
    }
    if (country_code.equals("NP") == true) {
      switch (region_code2) {
        case 1:
        name = "Bagmati";
        break;
        case 2:
        name = "Bheri";
        break;
        case 3:
        name = "Dhawalagiri";
        break;
        case 4:
        name = "Gandaki";
        break;
        case 5:
        name = "Janakpur";
        break;
        case 6:
        name = "Karnali";
        break;
        case 7:
        name = "Kosi";
        break;
        case 8:
        name = "Lumbini";
        break;
        case 9:
        name = "Mahakali";
        break;
        case 10:
        name = "Mechi";
        break;
        case 11:
        name = "Narayani";
        break;
        case 12:
        name = "Rapti";
        break;
        case 13:
        name = "Sagarmatha";
        break;
        case 14:
        name = "Seti";
        break;
      }
    }
    if (country_code.equals("NR") == true) {
      switch (region_code2) {
        case 1:
        name = "Aiwo";
        break;
        case 2:
        name = "Anabar";
        break;
        case 3:
        name = "Anetan";
        break;
        case 4:
        name = "Anibare";
        break;
        case 5:
        name = "Baiti";
        break;
        case 6:
        name = "Boe";
        break;
        case 7:
        name = "Buada";
        break;
        case 8:
        name = "Denigomodu";
        break;
        case 9:
        name = "Ewa";
        break;
        case 10:
        name = "Ijuw";
        break;
        case 11:
        name = "Meneng";
        break;
        case 12:
        name = "Nibok";
        break;
        case 13:
        name = "Uaboe";
        break;
        case 14:
        name = "Yaren";
        break;
      }
    }
    if (country_code.equals("NZ") == true) {
      switch (region_code2) {
        case 10:
        name = "Chatham Islands";
        break;
        case 1010:
        name = "Auckland";
        break;
        case 1011:
        name = "Bay of Plenty";
        break;
        case 1012:
        name = "Canterbury";
        break;
        case 1047:
        name = "Gisborne";
        break;
        case 1048:
        name = "Hawke's Bay";
        break;
        case 1049:
        name = "Manawatu-Wanganui";
        break;
        case 1050:
        name = "Marlborough";
        break;
        case 1051:
        name = "Nelson";
        break;
        case 1052:
        name = "Northland";
        break;
        case 1053:
        name = "Otago";
        break;
        case 1054:
        name = "Southland";
        break;
        case 1055:
        name = "Taranaki";
        break;
        case 1090:
        name = "Waikato";
        break;
        case 1091:
        name = "Wellington";
        break;
        case 1092:
        name = "West Coast";
        break;
        case 85:
        name = "Waikato";
        break;
      }
    }
    if (country_code.equals("OM") == true) {
      switch (region_code2) {
        case 1:
        name = "Ad Dakhiliyah";
        break;
        case 2:
        name = "Al Batinah";
        break;
        case 3:
        name = "Al Wusta";
        break;
        case 4:
        name = "Ash Sharqiyah";
        break;
        case 5:
        name = "Az Zahirah";
        break;
        case 6:
        name = "Masqat";
        break;
        case 7:
        name = "Musandam";
        break;
        case 8:
        name = "Zufar";
        break;
      }
    }
    if (country_code.equals("PA") == true) {
      switch (region_code2) {
        case 1:
        name = "Bocas del Toro";
        break;
        case 2:
        name = "Chiriqui";
        break;
        case 3:
        name = "Cocle";
        break;
        case 4:
        name = "Colon";
        break;
        case 5:
        name = "Darien";
        break;
        case 6:
        name = "Herrera";
        break;
        case 7:
        name = "Los Santos";
        break;
        case 8:
        name = "Panama";
        break;
        case 9:
        name = "San Blas";
        break;
        case 10:
        name = "Veraguas";
        break;
      }
    }
    if (country_code.equals("PE") == true) {
      switch (region_code2) {
        case 1:
        name = "Amazonas";
        break;
        case 2:
        name = "Ancash";
        break;
        case 3:
        name = "Apurimac";
        break;
        case 4:
        name = "Arequipa";
        break;
        case 5:
        name = "Ayacucho";
        break;
        case 6:
        name = "Cajamarca";
        break;
        case 7:
        name = "Callao";
        break;
        case 8:
        name = "Cusco";
        break;
        case 9:
        name = "Huancavelica";
        break;
        case 10:
        name = "Huanuco";
        break;
        case 11:
        name = "Ica";
        break;
        case 12:
        name = "Junin";
        break;
        case 13:
        name = "La Libertad";
        break;
        case 14:
        name = "Lambayeque";
        break;
        case 15:
        name = "Lima";
        break;
        case 16:
        name = "Loreto";
        break;
        case 17:
        name = "Madre de Dios";
        break;
        case 18:
        name = "Moquegua";
        break;
        case 19:
        name = "Pasco";
        break;
        case 20:
        name = "Piura";
        break;
        case 21:
        name = "Puno";
        break;
        case 22:
        name = "San Martin";
        break;
        case 23:
        name = "Tacna";
        break;
        case 24:
        name = "Tumbes";
        break;
        case 25:
        name = "Ucayali";
        break;
      }
    }
    if (country_code.equals("PG") == true) {
      switch (region_code2) {
        case 1:
        name = "Central";
        break;
        case 2:
        name = "Gulf";
        break;
        case 3:
        name = "Milne Bay";
        break;
        case 4:
        name = "Northern";
        break;
        case 5:
        name = "Southern Highlands";
        break;
        case 6:
        name = "Western";
        break;
        case 7:
        name = "North Solomons";
        break;
        case 8:
        name = "Chimbu";
        break;
        case 9:
        name = "Eastern Highlands";
        break;
        case 10:
        name = "East New Britain";
        break;
        case 11:
        name = "East Sepik";
        break;
        case 12:
        name = "Madang";
        break;
        case 13:
        name = "Manus";
        break;
        case 14:
        name = "Morobe";
        break;
        case 15:
        name = "New Ireland";
        break;
        case 16:
        name = "Western Highlands";
        break;
        case 17:
        name = "West New Britain";
        break;
        case 18:
        name = "Sandaun";
        break;
        case 19:
        name = "Enga";
        break;
        case 20:
        name = "National Capital";
        break;
      }
    }
    if (country_code.equals("PH") == true) {
      switch (region_code2) {
        case 1:
        name = "Abra";
        break;
        case 2:
        name = "Agusan del Norte";
        break;
        case 3:
        name = "Agusan del Sur";
        break;
        case 4:
        name = "Aklan";
        break;
        case 5:
        name = "Albay";
        break;
        case 6:
        name = "Antique";
        break;
        case 7:
        name = "Bataan";
        break;
        case 8:
        name = "Batanes";
        break;
        case 9:
        name = "Batangas";
        break;
        case 10:
        name = "Benguet";
        break;
        case 11:
        name = "Bohol";
        break;
        case 12:
        name = "Bukidnon";
        break;
        case 13:
        name = "Bulacan";
        break;
        case 14:
        name = "Cagayan";
        break;
        case 15:
        name = "Camarines Norte";
        break;
        case 16:
        name = "Camarines Sur";
        break;
        case 17:
        name = "Camiguin";
        break;
        case 18:
        name = "Capiz";
        break;
        case 19:
        name = "Catanduanes";
        break;
        case 20:
        name = "Cavite";
        break;
        case 21:
        name = "Cebu";
        break;
        case 22:
        name = "Basilan";
        break;
        case 23:
        name = "Eastern Samar";
        break;
        case 24:
        name = "Davao";
        break;
        case 25:
        name = "Davao del Sur";
        break;
        case 26:
        name = "Davao Oriental";
        break;
        case 27:
        name = "Ifugao";
        break;
        case 28:
        name = "Ilocos Norte";
        break;
        case 29:
        name = "Ilocos Sur";
        break;
        case 30:
        name = "Iloilo";
        break;
        case 31:
        name = "Isabela";
        break;
        case 32:
        name = "Kalinga-Apayao";
        break;
        case 33:
        name = "Laguna";
        break;
        case 34:
        name = "Lanao del Norte";
        break;
        case 35:
        name = "Lanao del Sur";
        break;
        case 36:
        name = "La Union";
        break;
        case 37:
        name = "Leyte";
        break;
        case 38:
        name = "Marinduque";
        break;
        case 39:
        name = "Masbate";
        break;
        case 40:
        name = "Mindoro Occidental";
        break;
        case 41:
        name = "Mindoro Oriental";
        break;
        case 42:
        name = "Misamis Occidental";
        break;
        case 43:
        name = "Misamis Oriental";
        break;
        case 44:
        name = "Mountain";
        break;
        case 45:
        name = "Negros Occidental";
        break;
        case 46:
        name = "Negros Oriental";
        break;
        case 47:
        name = "Nueva Ecija";
        break;
        case 48:
        name = "Nueva Vizcaya";
        break;
        case 49:
        name = "Palawan";
        break;
        case 50:
        name = "Pampanga";
        break;
        case 51:
        name = "Pangasinan";
        break;
        case 53:
        name = "Rizal";
        break;
        case 54:
        name = "Romblon";
        break;
        case 55:
        name = "Samar";
        break;
        case 56:
        name = "Maguindanao";
        break;
        case 57:
        name = "North Cotabato";
        break;
        case 58:
        name = "Sorsogon";
        break;
        case 59:
        name = "Southern Leyte";
        break;
        case 60:
        name = "Sulu";
        break;
        case 61:
        name = "Surigao del Norte";
        break;
        case 62:
        name = "Surigao del Sur";
        break;
        case 63:
        name = "Tarlac";
        break;
        case 64:
        name = "Zambales";
        break;
        case 65:
        name = "Zamboanga del Norte";
        break;
        case 66:
        name = "Zamboanga del Sur";
        break;
        case 67:
        name = "Northern Samar";
        break;
        case 68:
        name = "Quirino";
        break;
        case 69:
        name = "Siquijor";
        break;
        case 70:
        name = "South Cotabato";
        break;
        case 71:
        name = "Sultan Kudarat";
        break;
        case 72:
        name = "Tawitawi";
        break;
        case 832:
        name = "Angeles";
        break;
        case 833:
        name = "Bacolod";
        break;
        case 834:
        name = "Bago";
        break;
        case 835:
        name = "Baguio";
        break;
        case 836:
        name = "Bais";
        break;
        case 837:
        name = "Basilan City";
        break;
        case 838:
        name = "Batangas City";
        break;
        case 839:
        name = "Butuan";
        break;
        case 840:
        name = "Cabanatuan";
        break;
        case 875:
        name = "Cadiz";
        break;
        case 876:
        name = "Cagayan de Oro";
        break;
        case 877:
        name = "Calbayog";
        break;
        case 878:
        name = "Caloocan";
        break;
        case 879:
        name = "Canlaon";
        break;
        case 880:
        name = "Cavite City";
        break;
        case 881:
        name = "Cebu City";
        break;
        case 882:
        name = "Cotabato";
        break;
        case 883:
        name = "Dagupan";
        break;
        case 918:
        name = "Danao";
        break;
        case 919:
        name = "Dapitan";
        break;
        case 920:
        name = "Davao City";
        break;
        case 921:
        name = "Dipolog";
        break;
        case 922:
        name = "Dumaguete";
        break;
        case 923:
        name = "General Santos";
        break;
        case 924:
        name = "Gingoog";
        break;
        case 925:
        name = "Iligan";
        break;
        case 926:
        name = "Iloilo City";
        break;
        case 961:
        name = "Iriga";
        break;
        case 962:
        name = "La Carlota";
        break;
        case 963:
        name = "Laoag";
        break;
        case 964:
        name = "Lapu-Lapu";
        break;
        case 965:
        name = "Legaspi";
        break;
        case 966:
        name = "Lipa";
        break;
        case 967:
        name = "Lucena";
        break;
        case 968:
        name = "Mandaue";
        break;
        case 969:
        name = "Manila";
        break;
        case 1004:
        name = "Marawi";
        break;
        case 1005:
        name = "Naga";
        break;
        case 1006:
        name = "Olongapo";
        break;
        case 1007:
        name = "Ormoc";
        break;
        case 1008:
        name = "Oroquieta";
        break;
        case 1009:
        name = "Ozamis";
        break;
        case 1010:
        name = "Pagadian";
        break;
        case 1011:
        name = "Palayan";
        break;
        case 1012:
        name = "Pasay";
        break;
        case 1047:
        name = "Puerto Princesa";
        break;
        case 1048:
        name = "Quezon City";
        break;
        case 1049:
        name = "Roxas";
        break;
        case 1050:
        name = "San Carlos";
        break;
        case 1051:
        name = "San Carlos";
        break;
        case 1052:
        name = "San Jose";
        break;
        case 1053:
        name = "San Pablo";
        break;
        case 1054:
        name = "Silay";
        break;
        case 1055:
        name = "Surigao";
        break;
        case 1090:
        name = "Tacloban";
        break;
        case 1091:
        name = "Tagaytay";
        break;
        case 1092:
        name = "Tagbilaran";
        break;
        case 1093:
        name = "Tangub";
        break;
        case 1094:
        name = "Toledo";
        break;
        case 1095:
        name = "Trece Martires";
        break;
        case 1096:
        name = "Zamboanga";
        break;
        case 1097:
        name = "Aurora";
        break;
        case 1134:
        name = "Quezon";
        break;
        case 1135:
        name = "Negros Occidental";
        break;
      }
    }
    if (country_code.equals("PK") == true) {
      switch (region_code2) {
        case 1:
        name = "Federally Administered Tribal Areas";
        break;
        case 2:
        name = "Balochistan";
        break;
        case 3:
        name = "North-West Frontier";
        break;
        case 4:
        name = "Punjab";
        break;
        case 5:
        name = "Sindh";
        break;
        case 6:
        name = "Azad Kashmir";
        break;
        case 7:
        name = "Northern Areas";
        break;
        case 8:
        name = "Islamabad";
        break;
      }
    }
    if (country_code.equals("PL") == true) {
      switch (region_code2) {
        case 72:
        name = "Dolnoslaskie";
        break;
        case 73:
        name = "Kujawsko-Pomorskie";
        break;
        case 74:
        name = "Lodzkie";
        break;
        case 75:
        name = "Lubelskie";
        break;
        case 76:
        name = "Lubuskie";
        break;
        case 77:
        name = "Malopolskie";
        break;
        case 78:
        name = "Mazowieckie";
        break;
        case 79:
        name = "Opolskie";
        break;
        case 80:
        name = "Podkarpackie";
        break;
        case 81:
        name = "Podlaskie";
        break;
        case 82:
        name = "Pomorskie";
        break;
        case 83:
        name = "Slaskie";
        break;
        case 84:
        name = "Swietokrzyskie";
        break;
        case 85:
        name = "Warminsko-Mazurskie";
        break;
        case 86:
        name = "Wielkopolskie";
        break;
        case 87:
        name = "Zachodniopomorskie";
        break;
      }
    }
    if (country_code.equals("PS") == true) {
      switch (region_code2) {
        case 1131:
        name = "Gaza";
        break;
        case 1798:
        name = "West Bank";
        break;
      }
    }
    if (country_code.equals("PT") == true) {
      switch (region_code2) {
        case 2:
        name = "Aveiro";
        break;
        case 3:
        name = "Beja";
        break;
        case 4:
        name = "Braga";
        break;
        case 5:
        name = "Braganca";
        break;
        case 6:
        name = "Castelo Branco";
        break;
        case 7:
        name = "Coimbra";
        break;
        case 8:
        name = "Evora";
        break;
        case 9:
        name = "Faro";
        break;
        case 10:
        name = "Madeira";
        break;
        case 11:
        name = "Guarda";
        break;
        case 13:
        name = "Leiria";
        break;
        case 14:
        name = "Lisboa";
        break;
        case 16:
        name = "Portalegre";
        break;
        case 17:
        name = "Porto";
        break;
        case 18:
        name = "Santarem";
        break;
        case 19:
        name = "Setubal";
        break;
        case 20:
        name = "Viana do Castelo";
        break;
        case 21:
        name = "Vila Real";
        break;
        case 22:
        name = "Viseu";
        break;
        case 23:
        name = "Azores";
        break;
      }
    }
    if (country_code.equals("PY") == true) {
      switch (region_code2) {
        case 1:
        name = "Alto Parana";
        break;
        case 2:
        name = "Amambay";
        break;
        case 3:
        name = "Boqueron";
        break;
        case 4:
        name = "Caaguazu";
        break;
        case 5:
        name = "Caazapa";
        break;
        case 6:
        name = "Central";
        break;
        case 7:
        name = "Concepcion";
        break;
        case 8:
        name = "Cordillera";
        break;
        case 10:
        name = "Guaira";
        break;
        case 11:
        name = "Itapua";
        break;
        case 12:
        name = "Misiones";
        break;
        case 13:
        name = "Neembucu";
        break;
        case 15:
        name = "Paraguari";
        break;
        case 16:
        name = "Presidente Hayes";
        break;
        case 17:
        name = "San Pedro";
        break;
        case 19:
        name = "Canindeyu";
        break;
        case 20:
        name = "Chaco";
        break;
        case 21:
        name = "Nueva Asuncion";
        break;
        case 23:
        name = "Alto Paraguay";
        break;
      }
    }
    if (country_code.equals("QA") == true) {
      switch (region_code2) {
        case 1:
        name = "Ad Dawhah";
        break;
        case 2:
        name = "Al Ghuwariyah";
        break;
        case 3:
        name = "Al Jumaliyah";
        break;
        case 4:
        name = "Al Khawr";
        break;
        case 5:
        name = "Al Wakrah Municipality";
        break;
        case 6:
        name = "Ar Rayyan";
        break;
        case 8:
        name = "Madinat ach Shamal";
        break;
        case 9:
        name = "Umm Salal";
        break;
        case 10:
        name = "Al Wakrah";
        break;
        case 11:
        name = "Jariyan al Batnah";
        break;
        case 12:
        name = "Umm Sa'id";
        break;
      }
    }
    if (country_code.equals("RO") == true) {
      switch (region_code2) {
        case 1:
        name = "Alba";
        break;
        case 2:
        name = "Arad";
        break;
        case 3:
        name = "Arges";
        break;
        case 4:
        name = "Bacau";
        break;
        case 5:
        name = "Bihor";
        break;
        case 6:
        name = "Bistrita-Nasaud";
        break;
        case 7:
        name = "Botosani";
        break;
        case 8:
        name = "Braila";
        break;
        case 9:
        name = "Brasov";
        break;
        case 10:
        name = "Bucuresti";
        break;
        case 11:
        name = "Buzau";
        break;
        case 12:
        name = "Caras-Severin";
        break;
        case 13:
        name = "Cluj";
        break;
        case 14:
        name = "Constanta";
        break;
        case 15:
        name = "Covasna";
        break;
        case 16:
        name = "Dambovita";
        break;
        case 17:
        name = "Dolj";
        break;
        case 18:
        name = "Galati";
        break;
        case 19:
        name = "Gorj";
        break;
        case 20:
        name = "Harghita";
        break;
        case 21:
        name = "Hunedoara";
        break;
        case 22:
        name = "Ialomita";
        break;
        case 23:
        name = "Iasi";
        break;
        case 25:
        name = "Maramures";
        break;
        case 26:
        name = "Mehedinti";
        break;
        case 27:
        name = "Mures";
        break;
        case 28:
        name = "Neamt";
        break;
        case 29:
        name = "Olt";
        break;
        case 30:
        name = "Prahova";
        break;
        case 31:
        name = "Salaj";
        break;
        case 32:
        name = "Satu Mare";
        break;
        case 33:
        name = "Sibiu";
        break;
        case 34:
        name = "Suceava";
        break;
        case 35:
        name = "Teleorman";
        break;
        case 36:
        name = "Timis";
        break;
        case 37:
        name = "Tulcea";
        break;
        case 38:
        name = "Vaslui";
        break;
        case 39:
        name = "Valcea";
        break;
        case 40:
        name = "Vrancea";
        break;
        case 41:
        name = "Calarasi";
        break;
        case 42:
        name = "Giurgiu";
        break;
        case 43:
        name = "Ilfov";
        break;
      }
    }
    if (country_code.equals("RS") == true) {
      switch (region_code2) {
        case 1:
        name = "Kosovo";
        break;
        case 2:
        name = "Vojvodina";
        break;
      }
    }
    if (country_code.equals("RU") == true) {
      switch (region_code2) {
        case 1:
        name = "Adygeya";
        break;
        case 2:
        name = "Aginsky Buryatsky AO";
        break;
        case 3:
        name = "Gorno-Altay";
        break;
        case 4:
        name = "Altaisky krai";
        break;
        case 5:
        name = "Amur";
        break;
        case 6:
        name = "Arkhangel'sk";
        break;
        case 7:
        name = "Astrakhan'";
        break;
        case 8:
        name = "Bashkortostan";
        break;
        case 9:
        name = "Belgorod";
        break;
        case 10:
        name = "Bryansk";
        break;
        case 11:
        name = "Buryat";
        break;
        case 12:
        name = "Chechnya";
        break;
        case 13:
        name = "Chelyabinsk";
        break;
        case 14:
        name = "Chita";
        break;
        case 15:
        name = "Chukot";
        break;
        case 16:
        name = "Chuvashia";
        break;
        case 17:
        name = "Dagestan";
        break;
        case 18:
        name = "Evenk";
        break;
        case 19:
        name = "Ingush";
        break;
        case 20:
        name = "Irkutsk";
        break;
        case 21:
        name = "Ivanovo";
        break;
        case 22:
        name = "Kabardin-Balkar";
        break;
        case 23:
        name = "Kaliningrad";
        break;
        case 24:
        name = "Kalmyk";
        break;
        case 25:
        name = "Kaluga";
        break;
        case 26:
        name = "Kamchatka";
        break;
        case 27:
        name = "Karachay-Cherkess";
        break;
        case 28:
        name = "Karelia";
        break;
        case 29:
        name = "Kemerovo";
        break;
        case 30:
        name = "Khabarovsk";
        break;
        case 31:
        name = "Khakass";
        break;
        case 32:
        name = "Khanty-Mansiy";
        break;
        case 33:
        name = "Kirov";
        break;
        case 34:
        name = "Komi";
        break;
        case 35:
        name = "Komi-Permyak";
        break;
        case 36:
        name = "Koryak";
        break;
        case 37:
        name = "Kostroma";
        break;
        case 38:
        name = "Krasnodar";
        break;
        case 39:
        name = "Krasnoyarsk";
        break;
        case 40:
        name = "Kurgan";
        break;
        case 41:
        name = "Kursk";
        break;
        case 42:
        name = "Leningrad";
        break;
        case 43:
        name = "Lipetsk";
        break;
        case 44:
        name = "Magadan";
        break;
        case 45:
        name = "Mariy-El";
        break;
        case 46:
        name = "Mordovia";
        break;
        case 47:
        name = "Moskva";
        break;
        case 48:
        name = "Moscow City";
        break;
        case 49:
        name = "Murmansk";
        break;
        case 50:
        name = "Nenets";
        break;
        case 51:
        name = "Nizhegorod";
        break;
        case 52:
        name = "Novgorod";
        break;
        case 53:
        name = "Novosibirsk";
        break;
        case 54:
        name = "Omsk";
        break;
        case 55:
        name = "Orenburg";
        break;
        case 56:
        name = "Orel";
        break;
        case 57:
        name = "Penza";
        break;
        case 58:
        name = "Perm'";
        break;
        case 59:
        name = "Primor'ye";
        break;
        case 60:
        name = "Pskov";
        break;
        case 61:
        name = "Rostov";
        break;
        case 62:
        name = "Ryazan'";
        break;
        case 63:
        name = "Sakha";
        break;
        case 64:
        name = "Sakhalin";
        break;
        case 65:
        name = "Samara";
        break;
        case 66:
        name = "Saint Petersburg City";
        break;
        case 67:
        name = "Saratov";
        break;
        case 68:
        name = "North Ossetia";
        break;
        case 69:
        name = "Smolensk";
        break;
        case 70:
        name = "Stavropol'";
        break;
        case 71:
        name = "Sverdlovsk";
        break;
        case 72:
        name = "Tambovskaya oblast";
        break;
        case 73:
        name = "Tatarstan";
        break;
        case 74:
        name = "Taymyr";
        break;
        case 75:
        name = "Tomsk";
        break;
        case 76:
        name = "Tula";
        break;
        case 77:
        name = "Tver'";
        break;
        case 78:
        name = "Tyumen'";
        break;
        case 79:
        name = "Tuva";
        break;
        case 80:
        name = "Udmurt";
        break;
        case 81:
        name = "Ul'yanovsk";
        break;
        case 82:
        name = "Ust-Orda Buryat";
        break;
        case 83:
        name = "Vladimir";
        break;
        case 84:
        name = "Volgograd";
        break;
        case 85:
        name = "Vologda";
        break;
        case 86:
        name = "Voronezh";
        break;
        case 87:
        name = "Yamal-Nenets";
        break;
        case 88:
        name = "Yaroslavl'";
        break;
        case 89:
        name = "Yevrey";
        break;
        case 90:
        name = "Permskiy Kray";
        break;
        case 91:
        name = "Krasnoyarskiy Kray";
        break;
        case 942:
        name = "Chechnya Republic";
        break;
      }
    }
    if (country_code.equals("RW") == true) {
      switch (region_code2) {
        case 1:
        name = "Butare";
        break;
        case 6:
        name = "Gitarama";
        break;
        case 7:
        name = "Kibungo";
        break;
        case 9:
        name = "Kigali";
        break;
        case 11:
        name = "Est";
        break;
        case 12:
        name = "Kigali";
        break;
        case 13:
        name = "Nord";
        break;
        case 14:
        name = "Ouest";
        break;
        case 15:
        name = "Sud";
        break;
      }
    }
    if (country_code.equals("SA") == true) {
      switch (region_code2) {
        case 2:
        name = "Al Bahah";
        break;
        case 3:
        name = "Al Jawf";
        break;
        case 5:
        name = "Al Madinah";
        break;
        case 6:
        name = "Ash Sharqiyah";
        break;
        case 8:
        name = "Al Qasim";
        break;
        case 9:
        name = "Al Qurayyat";
        break;
        case 10:
        name = "Ar Riyad";
        break;
        case 13:
        name = "Ha'il";
        break;
        case 14:
        name = "Makkah";
        break;
        case 15:
        name = "Al Hudud ash Shamaliyah";
        break;
        case 16:
        name = "Najran";
        break;
        case 17:
        name = "Jizan";
        break;
        case 19:
        name = "Tabuk";
        break;
        case 20:
        name = "Al Jawf";
        break;
      }
    }
    if (country_code.equals("SB") == true) {
      switch (region_code2) {
        case 3:
        name = "Malaita";
        break;
        case 6:
        name = "Guadalcanal";
        break;
        case 7:
        name = "Isabel";
        break;
        case 8:
        name = "Makira";
        break;
        case 9:
        name = "Temotu";
        break;
        case 10:
        name = "Central";
        break;
        case 11:
        name = "Western";
        break;
        case 12:
        name = "Choiseul";
        break;
        case 13:
        name = "Rennell and Bellona";
        break;
      }
    }
    if (country_code.equals("SC") == true) {
      switch (region_code2) {
        case 1:
        name = "Anse aux Pins";
        break;
        case 2:
        name = "Anse Boileau";
        break;
        case 3:
        name = "Anse Etoile";
        break;
        case 4:
        name = "Anse Louis";
        break;
        case 5:
        name = "Anse Royale";
        break;
        case 6:
        name = "Baie Lazare";
        break;
        case 7:
        name = "Baie Sainte Anne";
        break;
        case 8:
        name = "Beau Vallon";
        break;
        case 9:
        name = "Bel Air";
        break;
        case 10:
        name = "Bel Ombre";
        break;
        case 11:
        name = "Cascade";
        break;
        case 12:
        name = "Glacis";
        break;
        case 13:
        name = "Grand' Anse";
        break;
        case 14:
        name = "Grand' Anse";
        break;
        case 15:
        name = "La Digue";
        break;
        case 16:
        name = "La Riviere Anglaise";
        break;
        case 17:
        name = "Mont Buxton";
        break;
        case 18:
        name = "Mont Fleuri";
        break;
        case 19:
        name = "Plaisance";
        break;
        case 20:
        name = "Pointe La Rue";
        break;
        case 21:
        name = "Port Glaud";
        break;
        case 22:
        name = "Saint Louis";
        break;
        case 23:
        name = "Takamaka";
        break;
      }
    }
    if (country_code.equals("SD") == true) {
      switch (region_code2) {
        case 27:
        name = "Al Wusta";
        break;
        case 28:
        name = "Al Istiwa'iyah";
        break;
        case 29:
        name = "Al Khartum";
        break;
        case 30:
        name = "Ash Shamaliyah";
        break;
        case 31:
        name = "Ash Sharqiyah";
        break;
        case 32:
        name = "Bahr al Ghazal";
        break;
        case 33:
        name = "Darfur";
        break;
        case 34:
        name = "Kurdufan";
        break;
        case 35:
        name = "Upper Nile";
        break;
        case 40:
        name = "Al Wahadah State";
        break;
        case 44:
        name = "Central Equatoria State";
        break;
      }
    }
    if (country_code.equals("SE") == true) {
      switch (region_code2) {
        case 1:
        name = "Alvsborgs Lan";
        break;
        case 2:
        name = "Blekinge Lan";
        break;
        case 3:
        name = "Gavleborgs Lan";
        break;
        case 4:
        name = "Goteborgs och Bohus Lan";
        break;
        case 5:
        name = "Gotlands Lan";
        break;
        case 6:
        name = "Hallands Lan";
        break;
        case 7:
        name = "Jamtlands Lan";
        break;
        case 8:
        name = "Jonkopings Lan";
        break;
        case 9:
        name = "Kalmar Lan";
        break;
        case 10:
        name = "Dalarnas Lan";
        break;
        case 11:
        name = "Kristianstads Lan";
        break;
        case 12:
        name = "Kronobergs Lan";
        break;
        case 13:
        name = "Malmohus Lan";
        break;
        case 14:
        name = "Norrbottens Lan";
        break;
        case 15:
        name = "Orebro Lan";
        break;
        case 16:
        name = "Ostergotlands Lan";
        break;
        case 17:
        name = "Skaraborgs Lan";
        break;
        case 18:
        name = "Sodermanlands Lan";
        break;
        case 21:
        name = "Uppsala Lan";
        break;
        case 22:
        name = "Varmlands Lan";
        break;
        case 23:
        name = "Vasterbottens Lan";
        break;
        case 24:
        name = "Vasternorrlands Lan";
        break;
        case 25:
        name = "Vastmanlands Lan";
        break;
        case 26:
        name = "Stockholms Lan";
        break;
        case 27:
        name = "Skane Lan";
        break;
        case 28:
        name = "Vastra Gotaland";
        break;
      }
    }
    if (country_code.equals("SH") == true) {
      switch (region_code2) {
        case 1:
        name = "Ascension";
        break;
        case 2:
        name = "Saint Helena";
        break;
        case 3:
        name = "Tristan da Cunha";
        break;
      }
    }
    if (country_code.equals("SI") == true) {
      switch (region_code2) {
        case 1:
        name = "Ajdovscina";
        break;
        case 2:
        name = "Beltinci";
        break;
        case 3:
        name = "Bled";
        break;
        case 4:
        name = "Bohinj";
        break;
        case 5:
        name = "Borovnica";
        break;
        case 6:
        name = "Bovec";
        break;
        case 7:
        name = "Brda";
        break;
        case 8:
        name = "Brezice";
        break;
        case 9:
        name = "Brezovica";
        break;
        case 11:
        name = "Celje";
        break;
        case 12:
        name = "Cerklje na Gorenjskem";
        break;
        case 13:
        name = "Cerknica";
        break;
        case 14:
        name = "Cerkno";
        break;
        case 15:
        name = "Crensovci";
        break;
        case 16:
        name = "Crna na Koroskem";
        break;
        case 17:
        name = "Crnomelj";
        break;
        case 19:
        name = "Divaca";
        break;
        case 20:
        name = "Dobrepolje";
        break;
        case 22:
        name = "Dol pri Ljubljani";
        break;
        case 24:
        name = "Dornava";
        break;
        case 25:
        name = "Dravograd";
        break;
        case 26:
        name = "Duplek";
        break;
        case 27:
        name = "Gorenja Vas-Poljane";
        break;
        case 28:
        name = "Gorisnica";
        break;
        case 29:
        name = "Gornja Radgona";
        break;
        case 30:
        name = "Gornji Grad";
        break;
        case 31:
        name = "Gornji Petrovci";
        break;
        case 32:
        name = "Grosuplje";
        break;
        case 34:
        name = "Hrastnik";
        break;
        case 35:
        name = "Hrpelje-Kozina";
        break;
        case 36:
        name = "Idrija";
        break;
        case 37:
        name = "Ig";
        break;
        case 38:
        name = "Ilirska Bistrica";
        break;
        case 39:
        name = "Ivancna Gorica";
        break;
        case 40:
        name = "Izola-Isola";
        break;
        case 42:
        name = "Jursinci";
        break;
        case 44:
        name = "Kanal";
        break;
        case 45:
        name = "Kidricevo";
        break;
        case 46:
        name = "Kobarid";
        break;
        case 47:
        name = "Kobilje";
        break;
        case 49:
        name = "Komen";
        break;
        case 50:
        name = "Koper-Capodistria";
        break;
        case 51:
        name = "Kozje";
        break;
        case 52:
        name = "Kranj";
        break;
        case 53:
        name = "Kranjska Gora";
        break;
        case 54:
        name = "Krsko";
        break;
        case 55:
        name = "Kungota";
        break;
        case 57:
        name = "Lasko";
        break;
        case 61:
        name = "Ljubljana";
        break;
        case 62:
        name = "Ljubno";
        break;
        case 64:
        name = "Logatec";
        break;
        case 66:
        name = "Loski Potok";
        break;
        case 68:
        name = "Lukovica";
        break;
        case 71:
        name = "Medvode";
        break;
        case 72:
        name = "Menges";
        break;
        case 73:
        name = "Metlika";
        break;
        case 74:
        name = "Mezica";
        break;
        case 76:
        name = "Mislinja";
        break;
        case 77:
        name = "Moravce";
        break;
        case 78:
        name = "Moravske Toplice";
        break;
        case 79:
        name = "Mozirje";
        break;
        case 80:
        name = "Murska Sobota";
        break;
        case 81:
        name = "Muta";
        break;
        case 82:
        name = "Naklo";
        break;
        case 83:
        name = "Nazarje";
        break;
        case 84:
        name = "Nova Gorica";
        break;
        case 86:
        name = "Odranci";
        break;
        case 87:
        name = "Ormoz";
        break;
        case 88:
        name = "Osilnica";
        break;
        case 89:
        name = "Pesnica";
        break;
        case 91:
        name = "Pivka";
        break;
        case 92:
        name = "Podcetrtek";
        break;
        case 94:
        name = "Postojna";
        break;
        case 97:
        name = "Puconci";
        break;
        case 98:
        name = "Racam";
        break;
        case 99:
        name = "Radece";
        break;
        case 832:
        name = "Radenci";
        break;
        case 833:
        name = "Radlje ob Dravi";
        break;
        case 834:
        name = "Radovljica";
        break;
        case 837:
        name = "Rogasovci";
        break;
        case 838:
        name = "Rogaska Slatina";
        break;
        case 839:
        name = "Rogatec";
        break;
        case 875:
        name = "Semic";
        break;
        case 876:
        name = "Sencur";
        break;
        case 877:
        name = "Sentilj";
        break;
        case 878:
        name = "Sentjernej";
        break;
        case 880:
        name = "Sevnica";
        break;
        case 881:
        name = "Sezana";
        break;
        case 882:
        name = "Skocjan";
        break;
        case 883:
        name = "Skofja Loka";
        break;
        case 918:
        name = "Skofljica";
        break;
        case 919:
        name = "Slovenj Gradec";
        break;
        case 921:
        name = "Slovenske Konjice";
        break;
        case 922:
        name = "Smarje pri Jelsah";
        break;
        case 923:
        name = "Smartno ob Paki";
        break;
        case 924:
        name = "Sostanj";
        break;
        case 925:
        name = "Starse";
        break;
        case 926:
        name = "Store";
        break;
        case 961:
        name = "Sveti Jurij";
        break;
        case 962:
        name = "Tolmin";
        break;
        case 963:
        name = "Trbovlje";
        break;
        case 964:
        name = "Trebnje";
        break;
        case 965:
        name = "Trzic";
        break;
        case 966:
        name = "Turnisce";
        break;
        case 967:
        name = "Velenje";
        break;
        case 968:
        name = "Velike Lasce";
        break;
        case 1004:
        name = "Vipava";
        break;
        case 1005:
        name = "Vitanje";
        break;
        case 1006:
        name = "Vodice";
        break;
        case 1008:
        name = "Vrhnika";
        break;
        case 1009:
        name = "Vuzenica";
        break;
        case 1010:
        name = "Zagorje ob Savi";
        break;
        case 1012:
        name = "Zavrc";
        break;
        case 1047:
        name = "Zelezniki";
        break;
        case 1048:
        name = "Ziri";
        break;
        case 1049:
        name = "Zrece";
        break;
        case 1093:
        name = "Dobrova-Horjul-Polhov Gradec";
        break;
        case 1096:
        name = "Domzale";
        break;
        case 1136:
        name = "Jesenice";
        break;
        case 1138:
        name = "Kamnik";
        break;
        case 1139:
        name = "Kocevje";
        break;
        case 1177:
        name = "Kuzma";
        break;
        case 1178:
        name = "Lenart";
        break;
        case 1180:
        name = "Litija";
        break;
        case 1181:
        name = "Ljutomer";
        break;
        case 1182:
        name = "Loska Dolina";
        break;
        case 1184:
        name = "Luce";
        break;
        case 1219:
        name = "Majsperk";
        break;
        case 1220:
        name = "Maribor";
        break;
        case 1223:
        name = "Miren-Kostanjevica";
        break;
        case 1225:
        name = "Novo Mesto";
        break;
        case 1227:
        name = "Piran";
        break;
        case 1266:
        name = "Preddvor";
        break;
        case 1268:
        name = "Ptuj";
        break;
        case 1305:
        name = "Ribnica";
        break;
        case 1307:
        name = "Ruse";
        break;
        case 1311:
        name = "Sentjur pri Celju";
        break;
        case 1312:
        name = "Slovenska Bistrica";
        break;
        case 1392:
        name = "Videm";
        break;
        case 1393:
        name = "Vojnik";
        break;
        case 1395:
        name = "Zalec";
        break;
      }
    }
    if (country_code.equals("SK") == true) {
      switch (region_code2) {
        case 1:
        name = "Banska Bystrica";
        break;
        case 2:
        name = "Bratislava";
        break;
        case 3:
        name = "Kosice";
        break;
        case 4:
        name = "Nitra";
        break;
        case 5:
        name = "Presov";
        break;
        case 6:
        name = "Trencin";
        break;
        case 7:
        name = "Trnava";
        break;
        case 8:
        name = "Zilina";
        break;
      }
    }
    if (country_code.equals("SL") == true) {
      switch (region_code2) {
        case 1:
        name = "Eastern";
        break;
        case 2:
        name = "Northern";
        break;
        case 3:
        name = "Southern";
        break;
        case 4:
        name = "Western Area";
        break;
      }
    }
    if (country_code.equals("SM") == true) {
      switch (region_code2) {
        case 1:
        name = "Acquaviva";
        break;
        case 2:
        name = "Chiesanuova";
        break;
        case 3:
        name = "Domagnano";
        break;
        case 4:
        name = "Faetano";
        break;
        case 5:
        name = "Fiorentino";
        break;
        case 6:
        name = "Borgo Maggiore";
        break;
        case 7:
        name = "San Marino";
        break;
        case 8:
        name = "Monte Giardino";
        break;
        case 9:
        name = "Serravalle";
        break;
      }
    }
    if (country_code.equals("SN") == true) {
      switch (region_code2) {
        case 1:
        name = "Dakar";
        break;
        case 3:
        name = "Diourbel";
        break;
        case 4:
        name = "Saint-Louis";
        break;
        case 5:
        name = "Tambacounda";
        break;
        case 7:
        name = "Thies";
        break;
        case 8:
        name = "Louga";
        break;
        case 9:
        name = "Fatick";
        break;
        case 10:
        name = "Kaolack";
        break;
        case 11:
        name = "Kolda";
        break;
        case 12:
        name = "Ziguinchor";
        break;
        case 13:
        name = "Louga";
        break;
        case 14:
        name = "Saint-Louis";
        break;
        case 15:
        name = "Matam";
        break;
      }
    }
    if (country_code.equals("SO") == true) {
      switch (region_code2) {
        case 1:
        name = "Bakool";
        break;
        case 2:
        name = "Banaadir";
        break;
        case 3:
        name = "Bari";
        break;
        case 4:
        name = "Bay";
        break;
        case 5:
        name = "Galguduud";
        break;
        case 6:
        name = "Gedo";
        break;
        case 7:
        name = "Hiiraan";
        break;
        case 8:
        name = "Jubbada Dhexe";
        break;
        case 9:
        name = "Jubbada Hoose";
        break;
        case 10:
        name = "Mudug";
        break;
        case 11:
        name = "Nugaal";
        break;
        case 12:
        name = "Sanaag";
        break;
        case 13:
        name = "Shabeellaha Dhexe";
        break;
        case 14:
        name = "Shabeellaha Hoose";
        break;
        case 16:
        name = "Woqooyi Galbeed";
        break;
        case 18:
        name = "Nugaal";
        break;
        case 19:
        name = "Togdheer";
        break;
        case 20:
        name = "Woqooyi Galbeed";
        break;
        case 21:
        name = "Awdal";
        break;
        case 22:
        name = "Sool";
        break;
      }
    }
    if (country_code.equals("SR") == true) {
      switch (region_code2) {
        case 10:
        name = "Brokopondo";
        break;
        case 11:
        name = "Commewijne";
        break;
        case 12:
        name = "Coronie";
        break;
        case 13:
        name = "Marowijne";
        break;
        case 14:
        name = "Nickerie";
        break;
        case 15:
        name = "Para";
        break;
        case 16:
        name = "Paramaribo";
        break;
        case 17:
        name = "Saramacca";
        break;
        case 18:
        name = "Sipaliwini";
        break;
        case 19:
        name = "Wanica";
        break;
      }
    }
    if (country_code.equals("ST") == true) {
      switch (region_code2) {
        case 1:
        name = "Principe";
        break;
        case 2:
        name = "Sao Tome";
        break;
      }
    }
    if (country_code.equals("SV") == true) {
      switch (region_code2) {
        case 1:
        name = "Ahuachapan";
        break;
        case 2:
        name = "Cabanas";
        break;
        case 3:
        name = "Chalatenango";
        break;
        case 4:
        name = "Cuscatlan";
        break;
        case 5:
        name = "La Libertad";
        break;
        case 6:
        name = "La Paz";
        break;
        case 7:
        name = "La Union";
        break;
        case 8:
        name = "Morazan";
        break;
        case 9:
        name = "San Miguel";
        break;
        case 10:
        name = "San Salvador";
        break;
        case 11:
        name = "Santa Ana";
        break;
        case 12:
        name = "San Vicente";
        break;
        case 13:
        name = "Sonsonate";
        break;
        case 14:
        name = "Usulutan";
        break;
      }
    }
    if (country_code.equals("SY") == true) {
      switch (region_code2) {
        case 1:
        name = "Al Hasakah";
        break;
        case 2:
        name = "Al Ladhiqiyah";
        break;
        case 3:
        name = "Al Qunaytirah";
        break;
        case 4:
        name = "Ar Raqqah";
        break;
        case 5:
        name = "As Suwayda'";
        break;
        case 6:
        name = "Dar";
        break;
        case 7:
        name = "Dayr az Zawr";
        break;
        case 8:
        name = "Rif Dimashq";
        break;
        case 9:
        name = "Halab";
        break;
        case 10:
        name = "Hamah";
        break;
        case 11:
        name = "Hims";
        break;
        case 12:
        name = "Idlib";
        break;
        case 13:
        name = "Dimashq";
        break;
        case 14:
        name = "Tartus";
        break;
      }
    }
    if (country_code.equals("SZ") == true) {
      switch (region_code2) {
        case 1:
        name = "Hhohho";
        break;
        case 2:
        name = "Lubombo";
        break;
        case 3:
        name = "Manzini";
        break;
        case 4:
        name = "Shiselweni";
        break;
        case 5:
        name = "Praslin";
        break;
      }
    }
    if (country_code.equals("TD") == true) {
      switch (region_code2) {
        case 1:
        name = "Batha";
        break;
        case 2:
        name = "Biltine";
        break;
        case 3:
        name = "Borkou-Ennedi-Tibesti";
        break;
        case 4:
        name = "Chari-Baguirmi";
        break;
        case 5:
        name = "Guera";
        break;
        case 6:
        name = "Kanem";
        break;
        case 7:
        name = "Lac";
        break;
        case 8:
        name = "Logone Occidental";
        break;
        case 9:
        name = "Logone Oriental";
        break;
        case 10:
        name = "Mayo-Kebbi";
        break;
        case 11:
        name = "Moyen-Chari";
        break;
        case 12:
        name = "Ouaddai";
        break;
        case 13:
        name = "Salamat";
        break;
        case 14:
        name = "Tandjile";
        break;
      }
    }
    if (country_code.equals("TG") == true) {
      switch (region_code2) {
        case 9:
        name = "Lama-Kara";
        break;
        case 18:
        name = "Tsevie";
        break;
        case 22:
        name = "Centrale";
        break;
        case 23:
        name = "Kara";
        break;
        case 24:
        name = "Maritime";
        break;
        case 25:
        name = "Plateaux";
        break;
        case 26:
        name = "Savanes";
        break;
      }
    }
    if (country_code.equals("TH") == true) {
      switch (region_code2) {
        case 1:
        name = "Mae Hong Son";
        break;
        case 2:
        name = "Chiang Mai";
        break;
        case 3:
        name = "Chiang Rai";
        break;
        case 4:
        name = "Nan";
        break;
        case 5:
        name = "Lamphun";
        break;
        case 6:
        name = "Lampang";
        break;
        case 7:
        name = "Phrae";
        break;
        case 8:
        name = "Tak";
        break;
        case 9:
        name = "Sukhothai";
        break;
        case 10:
        name = "Uttaradit";
        break;
        case 11:
        name = "Kamphaeng Phet";
        break;
        case 12:
        name = "Phitsanulok";
        break;
        case 13:
        name = "Phichit";
        break;
        case 14:
        name = "Phetchabun";
        break;
        case 15:
        name = "Uthai Thani";
        break;
        case 16:
        name = "Nakhon Sawan";
        break;
        case 17:
        name = "Nong Khai";
        break;
        case 18:
        name = "Loei";
        break;
        case 20:
        name = "Sakon Nakhon";
        break;
        case 21:
        name = "Nakhon Phanom";
        break;
        case 22:
        name = "Khon Kaen";
        break;
        case 23:
        name = "Kalasin";
        break;
        case 24:
        name = "Maha Sarakham";
        break;
        case 25:
        name = "Roi Et";
        break;
        case 26:
        name = "Chaiyaphum";
        break;
        case 27:
        name = "Nakhon Ratchasima";
        break;
        case 28:
        name = "Buriram";
        break;
        case 29:
        name = "Surin";
        break;
        case 30:
        name = "Sisaket";
        break;
        case 31:
        name = "Narathiwat";
        break;
        case 32:
        name = "Chai Nat";
        break;
        case 33:
        name = "Sing Buri";
        break;
        case 34:
        name = "Lop Buri";
        break;
        case 35:
        name = "Ang Thong";
        break;
        case 36:
        name = "Phra Nakhon Si Ayutthaya";
        break;
        case 37:
        name = "Saraburi";
        break;
        case 38:
        name = "Nonthaburi";
        break;
        case 39:
        name = "Pathum Thani";
        break;
        case 40:
        name = "Krung Thep";
        break;
        case 41:
        name = "Phayao";
        break;
        case 42:
        name = "Samut Prakan";
        break;
        case 43:
        name = "Nakhon Nayok";
        break;
        case 44:
        name = "Chachoengsao";
        break;
        case 45:
        name = "Prachin Buri";
        break;
        case 46:
        name = "Chon Buri";
        break;
        case 47:
        name = "Rayong";
        break;
        case 48:
        name = "Chanthaburi";
        break;
        case 49:
        name = "Trat";
        break;
        case 50:
        name = "Kanchanaburi";
        break;
        case 51:
        name = "Suphan Buri";
        break;
        case 52:
        name = "Ratchaburi";
        break;
        case 53:
        name = "Nakhon Pathom";
        break;
        case 54:
        name = "Samut Songkhram";
        break;
        case 55:
        name = "Samut Sakhon";
        break;
        case 56:
        name = "Phetchaburi";
        break;
        case 57:
        name = "Prachuap Khiri Khan";
        break;
        case 58:
        name = "Chumphon";
        break;
        case 59:
        name = "Ranong";
        break;
        case 60:
        name = "Surat Thani";
        break;
        case 61:
        name = "Phangnga";
        break;
        case 62:
        name = "Phuket";
        break;
        case 63:
        name = "Krabi";
        break;
        case 64:
        name = "Nakhon Si Thammarat";
        break;
        case 65:
        name = "Trang";
        break;
        case 66:
        name = "Phatthalung";
        break;
        case 67:
        name = "Satun";
        break;
        case 68:
        name = "Songkhla";
        break;
        case 69:
        name = "Pattani";
        break;
        case 70:
        name = "Yala";
        break;
        case 71:
        name = "Ubon Ratchathani";
        break;
        case 72:
        name = "Yasothon";
        break;
        case 73:
        name = "Nakhon Phanom";
        break;
        case 75:
        name = "Ubon Ratchathani";
        break;
        case 76:
        name = "Udon Thani";
        break;
        case 77:
        name = "Amnat Charoen";
        break;
        case 78:
        name = "Mukdahan";
        break;
        case 79:
        name = "Nong Bua Lamphu";
        break;
        case 80:
        name = "Sa Kaeo";
        break;
      }
    }
    if (country_code.equals("TJ") == true) {
      switch (region_code2) {
        case 1:
        name = "Kuhistoni Badakhshon";
        break;
        case 2:
        name = "Khatlon";
        break;
        case 3:
        name = "Sughd";
        break;
      }
    }
    if (country_code.equals("TM") == true) {
      switch (region_code2) {
        case 1:
        name = "Ahal";
        break;
        case 2:
        name = "Balkan";
        break;
        case 3:
        name = "Dashoguz";
        break;
        case 4:
        name = "Lebap";
        break;
        case 5:
        name = "Mary";
        break;
      }
    }
    if (country_code.equals("TN") == true) {
      switch (region_code2) {
        case 2:
        name = "Kasserine";
        break;
        case 3:
        name = "Kairouan";
        break;
        case 6:
        name = "Jendouba";
        break;
        case 14:
        name = "El Kef";
        break;
        case 15:
        name = "Al Mahdia";
        break;
        case 16:
        name = "Al Munastir";
        break;
        case 17:
        name = "Bajah";
        break;
        case 18:
        name = "Bizerte";
        break;
        case 19:
        name = "Nabeul";
        break;
        case 22:
        name = "Siliana";
        break;
        case 23:
        name = "Sousse";
        break;
        case 26:
        name = "Ariana";
        break;
        case 27:
        name = "Ben Arous";
        break;
        case 28:
        name = "Madanin";
        break;
        case 29:
        name = "Gabes";
        break;
        case 30:
        name = "Gafsa";
        break;
        case 31:
        name = "Kebili";
        break;
        case 32:
        name = "Sfax";
        break;
        case 33:
        name = "Sidi Bou Zid";
        break;
        case 34:
        name = "Tataouine";
        break;
        case 35:
        name = "Tozeur";
        break;
        case 36:
        name = "Tunis";
        break;
        case 37:
        name = "Zaghouan";
        break;
        case 38:
        name = "Aiana";
        break;
        case 39:
        name = "Manouba";
        break;
      }
    }
    if (country_code.equals("TO") == true) {
      switch (region_code2) {
        case 1:
        name = "Ha";
        break;
        case 2:
        name = "Tongatapu";
        break;
        case 3:
        name = "Vava";
        break;
      }
    }
    if (country_code.equals("TR") == true) {
      switch (region_code2) {
        case 2:
        name = "Adiyaman";
        break;
        case 3:
        name = "Afyonkarahisar";
        break;
        case 4:
        name = "Agri";
        break;
        case 5:
        name = "Amasya";
        break;
        case 7:
        name = "Antalya";
        break;
        case 8:
        name = "Artvin";
        break;
        case 9:
        name = "Aydin";
        break;
        case 10:
        name = "Balikesir";
        break;
        case 11:
        name = "Bilecik";
        break;
        case 12:
        name = "Bingol";
        break;
        case 13:
        name = "Bitlis";
        break;
        case 14:
        name = "Bolu";
        break;
        case 15:
        name = "Burdur";
        break;
        case 16:
        name = "Bursa";
        break;
        case 17:
        name = "Canakkale";
        break;
        case 19:
        name = "Corum";
        break;
        case 20:
        name = "Denizli";
        break;
        case 21:
        name = "Diyarbakir";
        break;
        case 22:
        name = "Edirne";
        break;
        case 23:
        name = "Elazig";
        break;
        case 24:
        name = "Erzincan";
        break;
        case 25:
        name = "Erzurum";
        break;
        case 26:
        name = "Eskisehir";
        break;
        case 28:
        name = "Giresun";
        break;
        case 31:
        name = "Hatay";
        break;
        case 32:
        name = "Mersin";
        break;
        case 33:
        name = "Isparta";
        break;
        case 34:
        name = "Istanbul";
        break;
        case 35:
        name = "Izmir";
        break;
        case 37:
        name = "Kastamonu";
        break;
        case 38:
        name = "Kayseri";
        break;
        case 39:
        name = "Kirklareli";
        break;
        case 40:
        name = "Kirsehir";
        break;
        case 41:
        name = "Kocaeli";
        break;
        case 43:
        name = "Kutahya";
        break;
        case 44:
        name = "Malatya";
        break;
        case 45:
        name = "Manisa";
        break;
        case 46:
        name = "Kahramanmaras";
        break;
        case 48:
        name = "Mugla";
        break;
        case 49:
        name = "Mus";
        break;
        case 50:
        name = "Nevsehir";
        break;
        case 52:
        name = "Ordu";
        break;
        case 53:
        name = "Rize";
        break;
        case 54:
        name = "Sakarya";
        break;
        case 55:
        name = "Samsun";
        break;
        case 57:
        name = "Sinop";
        break;
        case 58:
        name = "Sivas";
        break;
        case 59:
        name = "Tekirdag";
        break;
        case 60:
        name = "Tokat";
        break;
        case 61:
        name = "Trabzon";
        break;
        case 62:
        name = "Tunceli";
        break;
        case 63:
        name = "Sanliurfa";
        break;
        case 64:
        name = "Usak";
        break;
        case 65:
        name = "Van";
        break;
        case 66:
        name = "Yozgat";
        break;
        case 68:
        name = "Ankara";
        break;
        case 69:
        name = "Gumushane";
        break;
        case 70:
        name = "Hakkari";
        break;
        case 71:
        name = "Konya";
        break;
        case 72:
        name = "Mardin";
        break;
        case 73:
        name = "Nigde";
        break;
        case 74:
        name = "Siirt";
        break;
        case 75:
        name = "Aksaray";
        break;
        case 76:
        name = "Batman";
        break;
        case 77:
        name = "Bayburt";
        break;
        case 78:
        name = "Karaman";
        break;
        case 79:
        name = "Kirikkale";
        break;
        case 80:
        name = "Sirnak";
        break;
        case 81:
        name = "Adana";
        break;
        case 82:
        name = "Cankiri";
        break;
        case 83:
        name = "Gaziantep";
        break;
        case 84:
        name = "Kars";
        break;
        case 85:
        name = "Zonguldak";
        break;
        case 86:
        name = "Ardahan";
        break;
        case 87:
        name = "Bartin";
        break;
        case 88:
        name = "Igdir";
        break;
        case 89:
        name = "Karabuk";
        break;
        case 90:
        name = "Kilis";
        break;
        case 91:
        name = "Osmaniye";
        break;
        case 92:
        name = "Yalova";
        break;
        case 93:
        name = "Duzce";
        break;
      }
    }
    if (country_code.equals("TT") == true) {
      switch (region_code2) {
        case 1:
        name = "Arima";
        break;
        case 2:
        name = "Caroni";
        break;
        case 3:
        name = "Mayaro";
        break;
        case 4:
        name = "Nariva";
        break;
        case 5:
        name = "Port-of-Spain";
        break;
        case 6:
        name = "Saint Andrew";
        break;
        case 7:
        name = "Saint David";
        break;
        case 8:
        name = "Saint George";
        break;
        case 9:
        name = "Saint Patrick";
        break;
        case 10:
        name = "San Fernando";
        break;
        case 11:
        name = "Tobago";
        break;
        case 12:
        name = "Victoria";
        break;
      }
    }
    if (country_code.equals("TW") == true) {
      switch (region_code2) {
        case 1:
        name = "Fu-chien";
        break;
        case 2:
        name = "Kao-hsiung";
        break;
        case 3:
        name = "T'ai-pei";
        break;
        case 4:
        name = "T'ai-wan";
        break;
      }
    }
    if (country_code.equals("TZ") == true) {
      switch (region_code2) {
        case 2:
        name = "Pwani";
        break;
        case 3:
        name = "Dodoma";
        break;
        case 4:
        name = "Iringa";
        break;
        case 5:
        name = "Kigoma";
        break;
        case 6:
        name = "Kilimanjaro";
        break;
        case 7:
        name = "Lindi";
        break;
        case 8:
        name = "Mara";
        break;
        case 9:
        name = "Mbeya";
        break;
        case 10:
        name = "Morogoro";
        break;
        case 11:
        name = "Mtwara";
        break;
        case 12:
        name = "Mwanza";
        break;
        case 13:
        name = "Pemba North";
        break;
        case 14:
        name = "Ruvuma";
        break;
        case 15:
        name = "Shinyanga";
        break;
        case 16:
        name = "Singida";
        break;
        case 17:
        name = "Tabora";
        break;
        case 18:
        name = "Tanga";
        break;
        case 19:
        name = "Kagera";
        break;
        case 20:
        name = "Pemba South";
        break;
        case 21:
        name = "Zanzibar Central";
        break;
        case 22:
        name = "Zanzibar North";
        break;
        case 23:
        name = "Dar es Salaam";
        break;
        case 24:
        name = "Rukwa";
        break;
        case 25:
        name = "Zanzibar Urban";
        break;
        case 26:
        name = "Arusha";
        break;
        case 27:
        name = "Manyara";
        break;
      }
    }
    if (country_code.equals("UA") == true) {
      switch (region_code2) {
        case 1:
        name = "Cherkas'ka Oblast'";
        break;
        case 2:
        name = "Chernihivs'ka Oblast'";
        break;
        case 3:
        name = "Chernivets'ka Oblast'";
        break;
        case 4:
        name = "Dnipropetrovs'ka Oblast'";
        break;
        case 5:
        name = "Donets'ka Oblast'";
        break;
        case 6:
        name = "Ivano-Frankivs'ka Oblast'";
        break;
        case 7:
        name = "Kharkivs'ka Oblast'";
        break;
        case 8:
        name = "Khersons'ka Oblast'";
        break;
        case 9:
        name = "Khmel'nyts'ka Oblast'";
        break;
        case 10:
        name = "Kirovohrads'ka Oblast'";
        break;
        case 11:
        name = "Krym";
        break;
        case 12:
        name = "Kyyiv";
        break;
        case 13:
        name = "Kyyivs'ka Oblast'";
        break;
        case 14:
        name = "Luhans'ka Oblast'";
        break;
        case 15:
        name = "L'vivs'ka Oblast'";
        break;
        case 16:
        name = "Mykolayivs'ka Oblast'";
        break;
        case 17:
        name = "Odes'ka Oblast'";
        break;
        case 18:
        name = "Poltavs'ka Oblast'";
        break;
        case 19:
        name = "Rivnens'ka Oblast'";
        break;
        case 20:
        name = "Sevastopol'";
        break;
        case 21:
        name = "Sums'ka Oblast'";
        break;
        case 22:
        name = "Ternopil's'ka Oblast'";
        break;
        case 23:
        name = "Vinnyts'ka Oblast'";
        break;
        case 24:
        name = "Volyns'ka Oblast'";
        break;
        case 25:
        name = "Zakarpats'ka Oblast'";
        break;
        case 26:
        name = "Zaporiz'ka Oblast'";
        break;
        case 27:
        name = "Zhytomyrs'ka Oblast'";
        break;
      }
    }
    if (country_code.equals("UG") == true) {
      switch (region_code2) {
        case 5:
        name = "Busoga";
        break;
        case 8:
        name = "Karamoja";
        break;
        case 12:
        name = "South Buganda";
        break;
        case 18:
        name = "Central";
        break;
        case 20:
        name = "Eastern";
        break;
        case 21:
        name = "Nile";
        break;
        case 22:
        name = "North Buganda";
        break;
        case 23:
        name = "Northern";
        break;
        case 24:
        name = "Southern";
        break;
        case 25:
        name = "Western";
        break;
        case 33:
        name = "Jinja";
        break;
        case 36:
        name = "Kalangala";
        break;
        case 37:
        name = "Kampala";
        break;
        case 42:
        name = "Kiboga";
        break;
        case 52:
        name = "Mbarara";
        break;
        case 56:
        name = "Mubende";
        break;
        case 65:
        name = "Adjumani";
        break;
        case 66:
        name = "Bugiri";
        break;
        case 67:
        name = "Busia";
        break;
        case 69:
        name = "Katakwi";
        break;
        case 71:
        name = "Masaka";
        break;
        case 73:
        name = "Nakasongola";
        break;
        case 74:
        name = "Sembabule";
        break;
        case 77:
        name = "Arua";
        break;
        case 78:
        name = "Iganga";
        break;
        case 79:
        name = "Kabarole";
        break;
        case 80:
        name = "Kaberamaido";
        break;
        case 81:
        name = "Kamwenge";
        break;
        case 82:
        name = "Kanungu";
        break;
        case 83:
        name = "Kayunga";
        break;
        case 84:
        name = "Kitgum";
        break;
        case 85:
        name = "Kyenjojo";
        break;
        case 86:
        name = "Mayuge";
        break;
        case 87:
        name = "Mbale";
        break;
        case 88:
        name = "Moroto";
        break;
        case 89:
        name = "Mpigi";
        break;
        case 90:
        name = "Mukono";
        break;
        case 91:
        name = "Nakapiripirit";
        break;
        case 92:
        name = "Pader";
        break;
        case 93:
        name = "Rukungiri";
        break;
        case 94:
        name = "Sironko";
        break;
        case 95:
        name = "Soroti";
        break;
        case 96:
        name = "Wakiso";
        break;
        case 97:
        name = "Yumbe";
        break;
      }
    }
    if (country_code.equals("UY") == true) {
      switch (region_code2) {
        case 1:
        name = "Artigas";
        break;
        case 2:
        name = "Canelones";
        break;
        case 3:
        name = "Cerro Largo";
        break;
        case 4:
        name = "Colonia";
        break;
        case 5:
        name = "Durazno";
        break;
        case 6:
        name = "Flores";
        break;
        case 7:
        name = "Florida";
        break;
        case 8:
        name = "Lavalleja";
        break;
        case 9:
        name = "Maldonado";
        break;
        case 10:
        name = "Montevideo";
        break;
        case 11:
        name = "Paysandu";
        break;
        case 12:
        name = "Rio Negro";
        break;
        case 13:
        name = "Rivera";
        break;
        case 14:
        name = "Rocha";
        break;
        case 15:
        name = "Salto";
        break;
        case 16:
        name = "San Jose";
        break;
        case 17:
        name = "Soriano";
        break;
        case 18:
        name = "Tacuarembo";
        break;
        case 19:
        name = "Treinta y Tres";
        break;
      }
    }
    if (country_code.equals("UZ") == true) {
      switch (region_code2) {
        case 1:
        name = "Andijon";
        break;
        case 2:
        name = "Bukhoro";
        break;
        case 3:
        name = "Farghona";
        break;
        case 4:
        name = "Jizzakh";
        break;
        case 5:
        name = "Khorazm";
        break;
        case 6:
        name = "Namangan";
        break;
        case 7:
        name = "Nawoiy";
        break;
        case 8:
        name = "Qashqadaryo";
        break;
        case 9:
        name = "Qoraqalpoghiston";
        break;
        case 10:
        name = "Samarqand";
        break;
        case 11:
        name = "Sirdaryo";
        break;
        case 12:
        name = "Surkhondaryo";
        break;
        case 13:
        name = "Toshkent";
        break;
        case 14:
        name = "Toshkent";
        break;
      }
    }
    if (country_code.equals("VC") == true) {
      switch (region_code2) {
        case 1:
        name = "Charlotte";
        break;
        case 2:
        name = "Saint Andrew";
        break;
        case 3:
        name = "Saint David";
        break;
        case 4:
        name = "Saint George";
        break;
        case 5:
        name = "Saint Patrick";
        break;
        case 6:
        name = "Grenadines";
        break;
      }
    }
    if (country_code.equals("VE") == true) {
      switch (region_code2) {
        case 1:
        name = "Amazonas";
        break;
        case 2:
        name = "Anzoategui";
        break;
        case 3:
        name = "Apure";
        break;
        case 4:
        name = "Aragua";
        break;
        case 5:
        name = "Barinas";
        break;
        case 6:
        name = "Bolivar";
        break;
        case 7:
        name = "Carabobo";
        break;
        case 8:
        name = "Cojedes";
        break;
        case 9:
        name = "Delta Amacuro";
        break;
        case 11:
        name = "Falcon";
        break;
        case 12:
        name = "Guarico";
        break;
        case 13:
        name = "Lara";
        break;
        case 14:
        name = "Merida";
        break;
        case 15:
        name = "Miranda";
        break;
        case 16:
        name = "Monagas";
        break;
        case 17:
        name = "Nueva Esparta";
        break;
        case 18:
        name = "Portuguesa";
        break;
        case 19:
        name = "Sucre";
        break;
        case 20:
        name = "Tachira";
        break;
        case 21:
        name = "Trujillo";
        break;
        case 22:
        name = "Yaracuy";
        break;
        case 23:
        name = "Zulia";
        break;
        case 24:
        name = "Dependencias Federales";
        break;
        case 25:
        name = "Distrito Federal";
        break;
        case 26:
        name = "Vargas";
        break;
      }
    }
    if (country_code.equals("VN") == true) {
      switch (region_code2) {
        case 1:
        name = "An Giang";
        break;
        case 2:
        name = "Bac Thai";
        break;
        case 3:
        name = "Ben Tre";
        break;
        case 4:
        name = "Binh Tri Thien";
        break;
        case 5:
        name = "Cao Bang";
        break;
        case 6:
        name = "Cuu Long";
        break;
        case 7:
        name = "Dac Lac";
        break;
        case 9:
        name = "Dong Thap";
        break;
        case 11:
        name = "Ha Bac";
        break;
        case 12:
        name = "Hai Hung";
        break;
        case 13:
        name = "Hai Phong";
        break;
        case 14:
        name = "Ha Nam Ninh";
        break;
        case 15:
        name = "Ha Noi";
        break;
        case 16:
        name = "Ha Son Binh";
        break;
        case 17:
        name = "Ha Tuyen";
        break;
        case 19:
        name = "Hoang Lien Son";
        break;
        case 20:
        name = "Ho Chi Minh";
        break;
        case 21:
        name = "Kien Giang";
        break;
        case 22:
        name = "Lai Chau";
        break;
        case 23:
        name = "Lam Dong";
        break;
        case 24:
        name = "Long An";
        break;
        case 25:
        name = "Minh Hai";
        break;
        case 26:
        name = "Nghe Tinh";
        break;
        case 27:
        name = "Nghia Binh";
        break;
        case 28:
        name = "Phu Khanh";
        break;
        case 29:
        name = "Quang Nam-Da Nang";
        break;
        case 30:
        name = "Quang Ninh";
        break;
        case 31:
        name = "Song Be";
        break;
        case 32:
        name = "Son La";
        break;
        case 33:
        name = "Tay Ninh";
        break;
        case 34:
        name = "Thanh Hoa";
        break;
        case 35:
        name = "Thai Binh";
        break;
        case 36:
        name = "Thuan Hai";
        break;
        case 37:
        name = "Tien Giang";
        break;
        case 38:
        name = "Vinh Phu";
        break;
        case 39:
        name = "Lang Son";
        break;
        case 40:
        name = "Dong Nai";
        break;
        case 43:
        name = "An Giang";
        break;
        case 44:
        name = "Dac Lac";
        break;
        case 45:
        name = "Dong Nai";
        break;
        case 46:
        name = "Dong Thap";
        break;
        case 47:
        name = "Kien Giang";
        break;
        case 48:
        name = "Minh Hai";
        break;
        case 49:
        name = "Song Be";
        break;
        case 50:
        name = "Vinh Phu";
        break;
        case 51:
        name = "Ha Noi";
        break;
        case 52:
        name = "Ho Chi Minh";
        break;
        case 53:
        name = "Ba Ria-Vung Tau";
        break;
        case 54:
        name = "Binh Dinh";
        break;
        case 55:
        name = "Binh Thuan";
        break;
        case 56:
        name = "Can Tho";
        break;
        case 57:
        name = "Gia Lai";
        break;
        case 58:
        name = "Ha Giang";
        break;
        case 59:
        name = "Ha Tay";
        break;
        case 60:
        name = "Ha Tinh";
        break;
        case 61:
        name = "Hoa Binh";
        break;
        case 62:
        name = "Khanh Hoa";
        break;
        case 63:
        name = "Kon Tum";
        break;
        case 64:
        name = "Quang Tri";
        break;
        case 65:
        name = "Nam Ha";
        break;
        case 66:
        name = "Nghe An";
        break;
        case 67:
        name = "Ninh Binh";
        break;
        case 68:
        name = "Ninh Thuan";
        break;
        case 69:
        name = "Phu Yen";
        break;
        case 70:
        name = "Quang Binh";
        break;
        case 71:
        name = "Quang Ngai";
        break;
        case 72:
        name = "Quang Tri";
        break;
        case 73:
        name = "Soc Trang";
        break;
        case 74:
        name = "Thua Thien";
        break;
        case 75:
        name = "Tra Vinh";
        break;
        case 76:
        name = "Tuyen Quang";
        break;
        case 77:
        name = "Vinh Long";
        break;
        case 78:
        name = "Da Nang";
        break;
        case 79:
        name = "Hai Duong";
        break;
        case 80:
        name = "Ha Nam";
        break;
        case 81:
        name = "Hung Yen";
        break;
        case 82:
        name = "Nam Dinh";
        break;
        case 83:
        name = "Phu Tho";
        break;
        case 84:
        name = "Quang Nam";
        break;
        case 85:
        name = "Thai Nguyen";
        break;
        case 86:
        name = "Vinh Puc Province";
        break;
        case 87:
        name = "Can Tho";
        break;
        case 88:
        name = "Dak Lak";
        break;
        case 89:
        name = "Lai Chau";
        break;
        case 90:
        name = "Lao Cai";
        break;
        case 91:
        name = "Dak Nong";
        break;
        case 92:
        name = "Dien Bien";
        break;
        case 93:
        name = "Hau Giang";
        break;
      }
    }
    if (country_code.equals("VU") == true) {
      switch (region_code2) {
        case 5:
        name = "Ambrym";
        break;
        case 6:
        name = "Aoba";
        break;
        case 7:
        name = "Torba";
        break;
        case 8:
        name = "Efate";
        break;
        case 9:
        name = "Epi";
        break;
        case 10:
        name = "Malakula";
        break;
        case 11:
        name = "Paama";
        break;
        case 12:
        name = "Pentecote";
        break;
        case 13:
        name = "Sanma";
        break;
        case 14:
        name = "Shepherd";
        break;
        case 15:
        name = "Tafea";
        break;
        case 16:
        name = "Malampa";
        break;
        case 17:
        name = "Penama";
        break;
        case 18:
        name = "Shefa";
        break;
      }
    }
    if (country_code.equals("WS") == true) {
      switch (region_code2) {
        case 2:
        name = "Aiga-i-le-Tai";
        break;
        case 3:
        name = "Atua";
        break;
        case 4:
        name = "Fa";
        break;
        case 5:
        name = "Gaga";
        break;
        case 6:
        name = "Va";
        break;
        case 7:
        name = "Gagaifomauga";
        break;
        case 8:
        name = "Palauli";
        break;
        case 9:
        name = "Satupa";
        break;
        case 10:
        name = "Tuamasaga";
        break;
        case 11:
        name = "Vaisigano";
        break;
      }
    }
    if (country_code.equals("YE") == true) {
      switch (region_code2) {
        case 1:
        name = "Abyan";
        break;
        case 2:
        name = "Adan";
        break;
        case 3:
        name = "Al Mahrah";
        break;
        case 4:
        name = "Hadramawt";
        break;
        case 5:
        name = "Shabwah";
        break;
        case 6:
        name = "Al Ghaydah";
        break;
        case 8:
        name = "Al Hudaydah";
        break;
        case 10:
        name = "Al Mahwit";
        break;
        case 11:
        name = "Dhamar";
        break;
        case 14:
        name = "Ma'rib";
        break;
        case 15:
        name = "Sa";
        break;
        case 16:
        name = "San";
        break;
        case 20:
        name = "Al Bayda'";
        break;
        case 21:
        name = "Al Jawf";
        break;
        case 22:
        name = "Hajjah";
        break;
        case 23:
        name = "Ibb";
        break;
        case 24:
        name = "Lahij";
        break;
        case 25:
        name = "Ta";
        break;
      }
    }
    if (country_code.equals("ZA") == true) {
      switch (region_code2) {
        case 1:
        name = "North-Western Province";
        break;
        case 2:
        name = "KwaZulu-Natal";
        break;
        case 3:
        name = "Free State";
        break;
        case 5:
        name = "Eastern Cape";
        break;
        case 6:
        name = "Gauteng";
        break;
        case 7:
        name = "Mpumalanga";
        break;
        case 8:
        name = "Northern Cape";
        break;
        case 9:
        name = "Limpopo";
        break;
        case 10:
        name = "North-West";
        break;
        case 11:
        name = "Western Cape";
        break;
      }
    }
    if (country_code.equals("ZM") == true) {
      switch (region_code2) {
        case 1:
        name = "Western";
        break;
        case 2:
        name = "Central";
        break;
        case 3:
        name = "Eastern";
        break;
        case 4:
        name = "Luapula";
        break;
        case 5:
        name = "Northern";
        break;
        case 6:
        name = "North-Western";
        break;
        case 7:
        name = "Southern";
        break;
        case 8:
        name = "Copperbelt";
        break;
        case 9:
        name = "Lusaka";
        break;
      }
    }
    if (country_code.equals("ZW") == true) {
      switch (region_code2) {
        case 1:
        name = "Manicaland";
        break;
        case 2:
        name = "Midlands";
        break;
        case 3:
        name = "Mashonaland Central";
        break;
        case 4:
        name = "Mashonaland East";
        break;
        case 5:
        name = "Mashonaland West";
        break;
        case 6:
        name = "Matabeleland North";
        break;
        case 7:
        name = "Matabeleland South";
        break;
        case 8:
        name = "Masvingo";
        break;
        case 9:
        name = "Bulawayo";
        break;
        case 10:
        name = "Harare";
        break;
      }
    }
    return name;
  }
}
