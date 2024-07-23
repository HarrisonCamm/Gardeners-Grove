package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WeatherService {
    @Value("${weather.api.key:#{null}}")
    private String apiKey;

    @Value("${weather.api.url:#{null}}")
    private String apiUrl;

    private final Map<String, String> countryCodes = getCountryCodeMap();

    private final RestTemplate restTemplate = new RestTemplate();

    Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private Weather parseWeatherJson(JsonNode node) {
        Weather weather = new Weather();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);

        weather.setDayOfWeek(sdf.format(new Date(node.get("dt").asLong() * 1000)));
        weather.setDate(new Date(node.get("dt").asLong() * 1000).toString());
        weather.setDescription(node.get("weather").get(0).get("description").asText());
        weather.setIcon(node.get("weather").get(0).get("icon").asText());
        weather.setTemperature(node.get("main").get("temp").asText());
        weather.setHumidity(node.get("main").get("humidity").asText());

        return weather;
    }

    public Weather getCurrentWeather(String city, String country) {
        String countryCode = countryCodes.get(country.toLowerCase());
        countryCode = (countryCode == null) ? "" : "," + countryCode;
        String location = city + countryCode;

        logger.info("Fetching current weather for " + location + " from " + apiUrl);
        try {
            String url = String.format("%sweather?q=%s&appid=%s&units=metric", apiUrl, location, apiKey);
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response);
            return parseWeatherJson(node);
        } catch (Exception e) {
            logger.info("Failed to fetch current weather for " + location + " from " + apiUrl);
            return null;
        }
    }

    public List<Weather> getForecast(String city, String country) {
        String countryCode = countryCodes.get(country.toLowerCase());
        countryCode = (countryCode == null) ? "" : "," + countryCode;
        String location = city + countryCode;

        logger.info("Fetching forecast for " + location + " from " + apiUrl);
        try {
            String url = String.format("%sforecast?q=%s&appid=%s&units=metric", apiUrl, location, apiKey);
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            JsonNode listNode = rootNode.get("list");
            List<Weather> forecast = new ArrayList<>();
            for (JsonNode forecastNode : listNode) {
                forecast.add(parseWeatherJson(forecastNode));
            }
            return forecast;
        } catch (Exception e) {
            logger.info("Failed to fetch forecast for " + location + " from " + apiUrl);
            return null;
        }
    }

    public static Map<String, String> getCountryCodeMap() {
        Map<String, String> countryCodeMap = new HashMap<>();
        countryCodeMap.put("afghanistan", "AF");
        countryCodeMap.put("albania", "AL");
        countryCodeMap.put("algeria", "DZ");
        countryCodeMap.put("andorra", "AD");
        countryCodeMap.put("angola", "AO");
        countryCodeMap.put("antigua and barbuda", "AG");
        countryCodeMap.put("argentina", "AR");
        countryCodeMap.put("armenia", "AM");
        countryCodeMap.put("australia", "AU");
        countryCodeMap.put("austria", "AT");
        countryCodeMap.put("azerbaijan", "AZ");
        countryCodeMap.put("bahamas", "BS");
        countryCodeMap.put("bahrain", "BH");
        countryCodeMap.put("bangladesh", "BD");
        countryCodeMap.put("barbados", "BB");
        countryCodeMap.put("belarus", "BY");
        countryCodeMap.put("belgium", "BE");
        countryCodeMap.put("belize", "BZ");
        countryCodeMap.put("benin", "BJ");
        countryCodeMap.put("bhutan", "BT");
        countryCodeMap.put("bolivia", "BO");
        countryCodeMap.put("bosnia and herzegovina", "BA");
        countryCodeMap.put("botswana", "BW");
        countryCodeMap.put("brazil", "BR");
        countryCodeMap.put("brunei", "BN");
        countryCodeMap.put("bulgaria", "BG");
        countryCodeMap.put("burkina faso", "BF");
        countryCodeMap.put("burundi", "BI");
        countryCodeMap.put("cabo verde", "CV");
        countryCodeMap.put("cambodia", "KH");
        countryCodeMap.put("cameroon", "CM");
        countryCodeMap.put("canada", "CA");
        countryCodeMap.put("central african republic", "CF");
        countryCodeMap.put("chad", "TD");
        countryCodeMap.put("chile", "CL");
        countryCodeMap.put("china", "CN");
        countryCodeMap.put("colombia", "CO");
        countryCodeMap.put("comoros", "KM");
        countryCodeMap.put("congo (congo-brazzaville)", "CG");
        countryCodeMap.put("costa rica", "CR");
        countryCodeMap.put("croatia", "HR");
        countryCodeMap.put("cuba", "CU");
        countryCodeMap.put("cyprus", "CY");
        countryCodeMap.put("czechia (czech republic)", "CZ");
        countryCodeMap.put("democratic republic of the congo", "CD");
        countryCodeMap.put("denmark", "DK");
        countryCodeMap.put("djibouti", "DJ");
        countryCodeMap.put("dominica", "DM");
        countryCodeMap.put("dominican republic", "DO");
        countryCodeMap.put("ecuador", "EC");
        countryCodeMap.put("egypt", "EG");
        countryCodeMap.put("el salvador", "SV");
        countryCodeMap.put("equatorial guinea", "GQ");
        countryCodeMap.put("eritrea", "ER");
        countryCodeMap.put("estonia", "EE");
        countryCodeMap.put("eswatini", "SZ");
        countryCodeMap.put("ethiopia", "ET");
        countryCodeMap.put("fiji", "FJ");
        countryCodeMap.put("finland", "FI");
        countryCodeMap.put("france", "FR");
        countryCodeMap.put("gabon", "GA");
        countryCodeMap.put("gambia", "GM");
        countryCodeMap.put("georgia", "GE");
        countryCodeMap.put("germany", "DE");
        countryCodeMap.put("ghana", "GH");
        countryCodeMap.put("greece", "GR");
        countryCodeMap.put("grenada", "GD");
        countryCodeMap.put("guatemala", "GT");
        countryCodeMap.put("guinea", "GN");
        countryCodeMap.put("guinea-bissau", "GW");
        countryCodeMap.put("guyana", "GY");
        countryCodeMap.put("haiti", "HT");
        countryCodeMap.put("honduras", "HN");
        countryCodeMap.put("hungary", "HU");
        countryCodeMap.put("iceland", "IS");
        countryCodeMap.put("india", "IN");
        countryCodeMap.put("indonesia", "ID");
        countryCodeMap.put("iran", "IR");
        countryCodeMap.put("iraq", "IQ");
        countryCodeMap.put("ireland", "IE");
        countryCodeMap.put("israel", "IL");
        countryCodeMap.put("italy", "IT");
        countryCodeMap.put("jamaica", "JM");
        countryCodeMap.put("japan", "JP");
        countryCodeMap.put("jordan", "JO");
        countryCodeMap.put("kazakhstan", "KZ");
        countryCodeMap.put("kenya", "KE");
        countryCodeMap.put("kiribati", "KI");
        countryCodeMap.put("kuwait", "KW");
        countryCodeMap.put("kyrgyzstan", "KG");
        countryCodeMap.put("laos", "LA");
        countryCodeMap.put("latvia", "LV");
        countryCodeMap.put("lebanon", "LB");
        countryCodeMap.put("lesotho", "LS");
        countryCodeMap.put("liberia", "LR");
        countryCodeMap.put("libya", "LY");
        countryCodeMap.put("liechtenstein", "LI");
        countryCodeMap.put("lithuania", "LT");
        countryCodeMap.put("luxembourg", "LU");
        countryCodeMap.put("madagascar", "MG");
        countryCodeMap.put("malawi", "MW");
        countryCodeMap.put("malaysia", "MY");
        countryCodeMap.put("maldives", "MV");
        countryCodeMap.put("mali", "ML");
        countryCodeMap.put("malta", "MT");
        countryCodeMap.put("marshall islands", "MH");
        countryCodeMap.put("mauritania", "MR");
        countryCodeMap.put("mauritius", "MU");
        countryCodeMap.put("mexico", "MX");
        countryCodeMap.put("micronesia", "FM");
        countryCodeMap.put("moldova", "MD");
        countryCodeMap.put("monaco", "MC");
        countryCodeMap.put("mongolia", "MN");
        countryCodeMap.put("montenegro", "ME");
        countryCodeMap.put("morocco", "MA");
        countryCodeMap.put("mozambique", "MZ");
        countryCodeMap.put("myanmar (formerly burma)", "MM");
        countryCodeMap.put("namibia", "NA");
        countryCodeMap.put("nauru", "NR");
        countryCodeMap.put("nepal", "NP");
        countryCodeMap.put("netherlands", "NL");
        countryCodeMap.put("new zealand", "NZ");
        countryCodeMap.put("nicaragua", "NI");
        countryCodeMap.put("niger", "NE");
        countryCodeMap.put("nigeria", "NG");
        countryCodeMap.put("north korea", "KP");
        countryCodeMap.put("north macedonia", "MK");
        countryCodeMap.put("norway", "NO");
        countryCodeMap.put("oman", "OM");
        countryCodeMap.put("pakistan", "PK");
        countryCodeMap.put("palau", "PW");
        countryCodeMap.put("palestine state", "PS");
        countryCodeMap.put("panama", "PA");
        countryCodeMap.put("papua new guinea", "PG");
        countryCodeMap.put("paraguay", "PY");
        countryCodeMap.put("peru", "PE");
        countryCodeMap.put("philippines", "PH");
        countryCodeMap.put("poland", "PL");
        countryCodeMap.put("portugal", "PT");
        countryCodeMap.put("qatar", "QA");
        countryCodeMap.put("romania", "RO");
        countryCodeMap.put("russia", "RU");
        countryCodeMap.put("rwanda", "RW");
        countryCodeMap.put("saint kitts and nevis", "KN");
        countryCodeMap.put("saint lucia", "LC");
        countryCodeMap.put("saint vincent and the grenadines", "VC");
        countryCodeMap.put("samoa", "WS");
        countryCodeMap.put("san marino", "SM");
        countryCodeMap.put("sao tome and principe", "ST");
        countryCodeMap.put("saudi arabia", "SA");
        countryCodeMap.put("senegal", "SN");
        countryCodeMap.put("serbia", "RS");
        countryCodeMap.put("seychelles", "SC");
        countryCodeMap.put("sierra leone", "SL");
        countryCodeMap.put("singapore", "SG");
        countryCodeMap.put("slovakia", "SK");
        countryCodeMap.put("slovenia", "SI");
        countryCodeMap.put("solomon islands", "SB");
        countryCodeMap.put("somalia", "SO");
        countryCodeMap.put("south africa", "ZA");
        countryCodeMap.put("south korea", "KR");
        countryCodeMap.put("south sudan", "SS");
        countryCodeMap.put("spain", "ES");
        countryCodeMap.put("sri lanka", "LK");
        countryCodeMap.put("sudan", "SD");
        countryCodeMap.put("suriname", "SR");
        countryCodeMap.put("sweden", "SE");
        countryCodeMap.put("switzerland", "CH");
        countryCodeMap.put("syria", "SY");
        countryCodeMap.put("taiwan", "TW");
        countryCodeMap.put("tajikistan", "TJ");
        countryCodeMap.put("tanzania", "TZ");
        countryCodeMap.put("thailand", "TH");
        countryCodeMap.put("timor-leste", "TL");
        countryCodeMap.put("togo", "TG");
        countryCodeMap.put("tonga", "TO");
        countryCodeMap.put("trinidad and tobago", "TT");
        countryCodeMap.put("tunisia", "TN");
        countryCodeMap.put("turkey", "TR");
        countryCodeMap.put("turkmenistan", "TM");
        countryCodeMap.put("tuvalu", "TV");
        countryCodeMap.put("uganda", "UG");
        countryCodeMap.put("ukraine", "UA");
        countryCodeMap.put("united arab emirates", "AE");
        countryCodeMap.put("united kingdom", "GB");
        countryCodeMap.put("united states of america", "US");
        countryCodeMap.put("uruguay", "UY");
        countryCodeMap.put("uzbekistan", "UZ");
        countryCodeMap.put("vanuatu", "VU");
        countryCodeMap.put("vatican city", "VA");
        countryCodeMap.put("venezuela", "VE");
        countryCodeMap.put("vietnam", "VN");
        countryCodeMap.put("yemen", "YE");
        countryCodeMap.put("zambia", "ZM");
        countryCodeMap.put("zimbabwe", "ZW");

        return countryCodeMap;
    }


}