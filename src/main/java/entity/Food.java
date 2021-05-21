package entity;

import hannotation.Column;
import hannotation.Entity;
import hannotation.Id;
import helper.SQLDataTypes;

import java.sql.Date;

@Entity(tableName = "foods")
public class Food {
    @Column(columnName = "id", columnType = SQLDataTypes.INTEGER)
    @Id(autoIncrement = true)
    private int id;
    @Column(columnName = "name", columnType = SQLDataTypes.VARCHAR255)
    private String name;
    @Column(columnName = "categoryId", columnType = SQLDataTypes.INTEGER)
    private int categoryId;
    @Column(columnName = "description", columnType = SQLDataTypes.VARCHAR255)
    private String description;
    @Column(columnName = "thumbnail", columnType = SQLDataTypes.VARCHAR255)
    private String thumbnail;
    @Column(columnName = "price", columnType = SQLDataTypes.DOUBLE)
    private Double price;
    @Column(columnName = "saleStartDate", columnType = SQLDataTypes.DATE)
    private Date saleStartDate;
    @Column(columnName = "updatedAt", columnType = SQLDataTypes.DATE)
    private Date updatedAt;
    @Column(columnName = "createdAt", columnType = SQLDataTypes.DATE)
    private Date createdAt;
    @Column(columnName = "status", columnType = SQLDataTypes.INTEGER)
    private int status;

    public String toStatus(int status) {
        switch (status) {
            case 0:
                return "Đã xóa";
            case 1:
                return "Đang Bán";
            case 2:
                return "Dừng bán";
            default:
                return "Không xác định";
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getSaleStartDate() {
        return saleStartDate;
    }

    public void setSaleStartDate(Date saleStartDate) {
        this.saleStartDate = saleStartDate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean validate() {
        if (this.getName().trim().length() > 7 && this.getPrice() > 0) {
            return true;
        };
        return false;
    }

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryId=" + categoryId +
                ", description='" + description + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", price=" + price +
                ", saleStartDate=" + saleStartDate +
                ", updatedAt=" + updatedAt +
                ", createdAt=" + createdAt +
                ", status=" + status +
                '}';
    }
}
