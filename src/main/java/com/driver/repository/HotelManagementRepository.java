package com.driver.repository;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Repository
public class HotelManagementRepository {
    Map<Integer, User> users=new HashMap<>();
    Map<String, Hotel> hotels=new HashMap<>();
    Map<String, Booking> bookings=new HashMap<>();
    Map<Integer, List<Booking>> perPersonBookings=new HashMap<>();

    public String addHotel(Hotel hotel){
        if(hotel.getHotelName()==null || hotel==null){
            return "FAILURE";
        }
        if(hotels.containsKey(hotel.getHotelName())){
            return "FAILURE";
        }
         hotels.put(hotel.getHotelName(),hotel);
          return "SUCCESS";
    }

    public Integer addUser(User user){

        users.put(user.getaadharCardNo(),user);

        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities(){
        TreeMap<String,Hotel> map= new TreeMap<>();
        map.putAll(hotels);

        String maxFacilityHotelName="";
        int maxFacility=0;

        for(String s:map.keySet()){
            int currSize=map.get(s).getFacilities().size();
            if(maxFacility<currSize){
                maxFacility=currSize;
                maxFacilityHotelName=s;
            }
        }

        return maxFacilityHotelName;
    }

    public int bookARoom(Booking booking){
        String primaryID =booking.getBookingId();
        bookings.put(primaryID,booking);
        int availableRooms=hotels.get(booking.getHotelName()).getAvailableRooms();
        if(booking.getNoOfRooms()>availableRooms){
            return -1;
        }
        List<Booking> bookingsTillNow = perPersonBookings.getOrDefault(booking.getBookingAadharCard(),new ArrayList<>());
        bookingsTillNow.add(booking);
        perPersonBookings.put(booking.getBookingAadharCard(),bookingsTillNow);
        int priceToBePaid = booking.getNoOfRooms() * hotels.get(booking.getHotelName()).getPricePerNight();
        booking.setAmountToBePaid(priceToBePaid);
        return priceToBePaid;
    }
    public int getBookings(Integer aadharCard)
    {
        return perPersonBookings.get(aadharCard).size();

    }

    public boolean availableFacilities(List<Facility> facilities,Facility facility){
            for(Facility f:facilities){
                if(f==facility){
                    return true;
                }
            }
            return false;
    }
    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName){
            Hotel currHotel=hotels.get(hotelName);
            List<Facility> current= currHotel.getFacilities();
            for(Facility fac:newFacilities){
                if(availableFacilities(current,fac)==false){
                    current.add(fac);
                }
            }
            currHotel.setFacilities(current);
        return currHotel;
    }
}
