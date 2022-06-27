package rs.etf.sab.student;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

public class StudentMain {

 
    

    public static void main(String[] args) {
                AddressOperations addressOperations = new sf180321_AddressOperations(); // Change this to your implementation.
                CityOperations cityOperations = new sf180321_CityOperations(); // Do it for all classes.
                CourierOperations courierOperations = new sf180321_CourierOperations(); // e.g. = new MyDistrictOperations();
                CourierRequestOperation courierRequestOperation = new sf180321_CourierRequestOperation();
                DriveOperation driveOperation = new sf180321_DriveOperation();
                GeneralOperations generalOperations = new sf180321_GeneralOperations();
                PackageOperations packageOperations = new sf180321_PackageOperations();
                StockroomOperations stockroomOperations = new sf180321_StockroomOperations();
                UserOperations userOperations = new sf180321_UserOperations();
                VehicleOperations vehicleOperations = new sf180321_VehicleOperations();
        
        
                TestHandler.createInstance(
                        addressOperations,
                        cityOperations,
                        courierOperations,
                        courierRequestOperation,
                        driveOperation,
                        generalOperations,
                        packageOperations,
                        stockroomOperations,
                        userOperations,
                        vehicleOperations);
        
                TestRunner.runTests();
       
        
    

    //MOJI TESTOVI
//        sf180321_CityOperations grad = new sf180321_CityOperations();
//        sf180321_AddressOperations adr = new sf180321_AddressOperations();
//        sf180321_StockroomOperations mag = new sf180321_StockroomOperations();
//        sf180321_UserOperations user = new sf180321_UserOperations();
//        sf180321_VehicleOperations vehicle = new sf180321_VehicleOperations();
//        sf180321_CourierRequestOperation req_courier=new sf180321_CourierRequestOperation();
//        sf180321_CourierOperations courier=new sf180321_CourierOperations();
//        sf180321_PackageOperations pak=new sf180321_PackageOperations();
//        sf180321_GeneralOperations gen=new sf180321_GeneralOperations();
//   user.insertUser("korime5", "Filip", "Starcevic", "NekaLozinka213$", 1);
    // System.out.println(req_courier.getAllCourierRequests());
    //System.out.println(vehicle.insertVehicle("123458", 0, BigDecimal.ZERO, BigDecimal.TEN));
    // System.out.println(user.insertUser("LololMen2", "LOL", "MEEN", "1234555sasSS$$", 1));
    //  System.out.println(req_courier.insertCourierRequest("LololMen", "abcdeef"));
    //System.out.println(courier.insertCourier("korime6", "123456787"));
    //System.out.println(vehicle.insertVehicle("askjwi2448", 2, BigDecimal.valueOf(4.33), BigDecimal.valueOf(1588)));
    //System.out.println(vehicle.deleteVehicles("askjwi2446","askjwi2445"));
    //System.out.println(vehicle.parkVehicle("dasdasdadasd", 1));
    // System.out.println(pak.insertPackage(2, 1, "korime6", 1, BigDecimal.valueOf(4.66)));
    //    System.out.println(adr.insertAddress("Mine Vukomanovic",  9, 1, 10, 80));
    // System.out.println(pak.insertPackage(2,1,"korime5",2,BigDecimal.valueOf(56)));
    // System.out.println(pak.acceptAnOffer(8));
}
}
