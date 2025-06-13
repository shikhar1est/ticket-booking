package ticket.booking.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserBookingService {
    private User user;

    private List<User> userList;

    private ObjectMapper objMapper = new ObjectMapper();

    private static final String USERS_PATH="../localDB/users.json";

    public UserBookingService() throws IOException{  //Default Constructor which loads up all Users
        loadUsers();
    }
    public UserBookingService(User user1) throws IOException {
        this.user=user1;
        loadUsers();
    }

    public List<User> loadUsers() throws IOException{
        File users=new File(USERS_PATH);
        return objMapper.readValue(users, new TypeReference<List<User>>() {});
    }
    public Boolean loginUser(){ //Checks if the user exists & password matches
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equalsIgnoreCase(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException{
        File usersFile=new File(USERS_PATH);
        objMapper.writeValue(usersFile,userList);
    }
    public void fetchBooking(){
        user.printTickets();
    }
    public Boolean cancelBooking(String ticketId){
        Scanner sc=new Scanner(System.in);
        System.out.println("Please enter your ticket ID you want to cancel");
        ticketId=sc.next();

        if(ticketId==null || ticketId.isEmpty()){
            System.out.println("Ticket ID cannot be empty");
            return Boolean.FALSE;
        }
        String finalTicketId=ticketId;
        Boolean cancel=user.getTicketsBooked().removeIf(ticket-> ticket.getTicketId().equals(finalTicketId));

        if(cancel){
            System.out.println("Your ticket with ID"+ ticketId + "has been cancelled");
            return Boolean.TRUE;
        }else{
            System.out.println("No ticket found withh ID"+ ticketId);
            return Boolean.FALSE;
        }
    }
    public List<Train> getTrains(String source,String destination) {
        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch(){

        }
    }
}
