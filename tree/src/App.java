import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class App {

   public String getPlan(LocalDate date, String weather, LocalTime firstAppointment, LocalTime lastAppointment) {

       if (isWeekend(date)) {
           if (isGoodWeather(weather) && canTakeTrainOnWeekend()) {
               return "Please take the train to go to the city, and train to get back home on " + date.toString() + ".";
           } else {
               return "Please drive on " + date.toString() + ", and leave the house at least an hour before your first appointment.";
           }
       } else {
           if (isGoodWeather(weather) && canTakeTrainOnWeekday(firstAppointment, lastAppointment)) {
               return "Please take the train to go to the city, and train to get back home on " + date.toString() + ".";
           } else {
               return "Please drive on " + date.toString() + ", and leave the house at least an hour before your first appointment.";
           }
       }
   }

   /*
    * Check if the given date is a weekend
    * @param date the date to check
    * @return true if the date is a weekend, false otherwise
    */
   private boolean isWeekend(LocalDate date) {
       return date.getDayOfWeek().getValue() >= 6; // Saturday or Sunday
   }

   /*
    * Check if the weather is good for taking the train
    * @param weather the weather prediction
    * @return true if the weather is good, false otherwise
    */
   private boolean isGoodWeather(String weather) {
       return !weather.equalsIgnoreCase("Rainy") && !weather.equalsIgnoreCase("Snowy");
   }

   /*
    * Check if the user can take the train on a weekday
    * @param firstAppointment the time of the first appointment
    * @param lastAppointment the time of the last appointment
    * @return true if the user can take the train, false otherwise
    */
   private boolean canTakeTrainOnWeekday(LocalTime firstAppointment, LocalTime lastAppointment) {

       LocalTime firstTrain = LocalTime.of(6, 0);
       LocalTime lastTrain = LocalTime.of(23, 0);

       return firstAppointment.isAfter(firstTrain) && lastAppointment.isBefore(lastTrain);
   }

   /*
    * Check if the user can take the train on a weekend
    * @return true if the user can take the train, false otherwise
    */
   private boolean canTakeTrainOnWeekend() {
       LocalTime firstTrain = LocalTime.of(10, 0);
       LocalTime lastTrain = LocalTime.of(22, 0);

       return LocalTime.now().isAfter(firstTrain) && LocalTime.now().isBefore(lastTrain);
   }

   public static void main(String[] args) {
       App planner = new App();

       Scanner scanner = new Scanner(System.in);
       LocalDate date;
       LocalTime firstAppointment;
       LocalTime lastAppointment;

       String weather;

       API_Handler api = new API_Handler();
        String response = api.pullAPI();
        System.out.println(response);

       // Input validation for date

       do {
           System.out.println("Enter the date (YYYY-MM-DD): ");

           try {
               date = LocalDate.parse(scanner.nextLine());
               break; // Break the loop if date is successfully parsed
           } catch (Exception e) {
               System.out.println("Invalid date format. Please enter in YYYY-MM-DD format.");
           }
       } while (true);

       // Input validation for weather

       do {
           System.out.println("Enter the weather prediction (Rainy/Snowy/Cloudy/Sunny): ");
           weather = scanner.nextLine();

           if (!weather.equalsIgnoreCase("Rainy") && !weather.equalsIgnoreCase("Snowy") &&
               !weather.equalsIgnoreCase("Cloudy") && !weather.equalsIgnoreCase("Sunny")) {
               System.out.println("Invalid weather prediction. Please enter Rainy, Snowy, Cloudy, or Sunny.");
           } else {
               break; // Break the loop if weather prediction is valid
           }
       } while (true);


       // Input validation for first appointment time

       do {
           System.out.println("Enter the time of the first appointment (HH:MM): ");

           try {
               firstAppointment = LocalTime.parse(scanner.nextLine());
               break; // Break the loop if time is successfully parsed
           } catch (Exception e) {
               System.out.println("Invalid time format. Please enter in HH:MM format.");
           }
       } while (true);


       // Input validation for last appointment time

       do {
           System.out.println("Enter the time of the last appointment (HH:MM): ");

           try {
               lastAppointment = LocalTime.parse(scanner.nextLine());
               break; // Break the loop if time is successfully parsed
           } catch (Exception e) {
               System.out.println("Invalid time format. Please enter in HH:MM format.");
           }
       } while (true);


       // Check if last appointment is after first appointment

       if (lastAppointment.isBefore(firstAppointment)) {
           System.out.println("Last appointment time should be after first appointment time.");
       }


       String plan = planner.getPlan(date, weather, firstAppointment, lastAppointment);

       System.out.println(plan);
       scanner.close();
   }

   public static class API_Handler {
    public static String pullAPI(){
        try {
            // Construct the URL
            String apiUrl = "https://api.tomorrow.io/v4/weather/forecast?location=boston&timesteps=daily&apikey=lTyr47R57VmoPFcX79tn87iz5KLYzI89";
            URL url = new URL(apiUrl);

            // Open the connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            connection.disconnect();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        }
    }
}