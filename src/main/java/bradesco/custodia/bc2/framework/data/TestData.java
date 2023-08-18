package bradesco.custodia.bc2.framework.data;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TestData {

    static Faker faker = new Faker(new Locale("en-IND"));

    public static void main(String[] args) {
        System.out.println(TestData.getFirstName());
    }

    public static int getTodaysDate() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.getDayOfMonth();
    }

    public static String getCurrentMonth() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.getMonth().toString();
    }

    public static int getCurrentMonthNo() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public static String getTodayDateInFormat(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        Date dateObj = calendar.getTime();
        String formattedDate = dateFormat.format(dateObj);
        System.out.println(formattedDate);
        return formattedDate;
    }

    public static int getCurrentYear() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.getYear();
    }

    public static String generateRandomAlphaNumericString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String generateRandomAlphabeticString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    public static String generateRandomNumericString(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    public static String generateEmail() {
        return faker.name().username() + "@mailinator.com";
    }

    public static String getFirstName() {
        return faker.name().firstName();
    }

    public static String getLastName() {
        return faker.name().lastName();
    }

    public static String getTimeStamp() {
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
    }
}
