package service;

import entity.Category;
import entity.Food;
import repository.GenericRepository;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

public class FoodService {

    private GenericRepository<Food> foodGenericRepository;
    public FoodService() {
        this.foodGenericRepository = new GenericRepository<>(Food.class);
    }
    java.sql.Date date = new java.sql.Date(System.currentTimeMillis());

    public boolean create(Food food){
        if(food.validate()){
            food.setSaleStartDate(date);
            food.setCreatedAt(date);
            food.setUpdatedAt(date);
            food.setStatus(1);
            foodGenericRepository.save(food);
            return true;
        }
        System.out.println("chưa nhạp dữ liệu");
        return false;
    };
    public boolean edit(String id, Food obj) {
        obj.setUpdatedAt(date);
        return foodGenericRepository.update(id, obj);
    }
    public boolean delete(String id, Food obj) {
        obj.setUpdatedAt(date);
        obj.setStatus(0);
        return foodGenericRepository.update(id, obj);
    }

    public List<Food> getList(int a, int b) {
        return foodGenericRepository.findAll(a,b);
    }
    public Food getById(String id) {
        return foodGenericRepository.findById(id);
    }

}
