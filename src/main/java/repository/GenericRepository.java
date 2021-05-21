package repository;

import hannotation.Column;
import hannotation.Entity;
import hannotation.Id;
import helper.ConnectionHelper;
import helper.ConvertHelper;
import helper.SQLConstant;
import helper.SQLDataTypes;


import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericRepository<T> {
    private Class<T> clazz;

    public GenericRepository(Class<T> clazz) {
        this.clazz = clazz;
    }

    private boolean isEntity() {
        return clazz.isAnnotationPresent(Entity.class);
    }

    public boolean save(T obj) {
        try {
            if (!isEntity()) {
                throw new MyModelError("Not an entity model check your annotation");
            }
            Entity currentEntity = (Entity) clazz.getAnnotation(Entity.class);
            //build sql cmd
            StringBuilder stringCmd = new StringBuilder();
            stringCmd.append(SQLConstant.INSERT_INTO);
            stringCmd.append(SQLConstant.SPACE);
            stringCmd.append(currentEntity.tableName());
            stringCmd.append(SQLConstant.SPACE);
            stringCmd.append(SQLConstant.OPEN_PARENTHESES);
            Field[] fields = clazz.getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (!field.isAnnotationPresent(Column.class)) {
                    continue;
                }
                Column currentColumn = field.getAnnotation(Column.class);
                //id checker
                if (field.isAnnotationPresent(Id.class)) {
                    Id currentId = (Id) field.getAnnotation(Id.class);
                    if (currentId.autoIncrement()) {
                        continue;
                    }
                }
                stringCmd.append(currentColumn.columnName());
                stringCmd.append(SQLConstant.COMMON);
                stringCmd.append(SQLConstant.SPACE);

            }
            stringCmd.setLength(stringCmd.length() - 2);
            stringCmd.append(SQLConstant.CLOSE_PARENTHESES);
            stringCmd.append(SQLConstant.SPACE);
            stringCmd.append(SQLConstant.VALUES);
            stringCmd.append(SQLConstant.SPACE);
            stringCmd.append(SQLConstant.OPEN_PARENTHESES);
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (!field.isAnnotationPresent(Column.class)) {
                    continue;
                }
                Column currentColumn = field.getAnnotation(Column.class);
                String columnType = currentColumn.columnType();
                field.setAccessible(true);
                Object value = field.get(obj);
                if (columnType.equals(SQLDataTypes.DATE)) {
                    Date date = (Date) value;
                    value = ConvertHelper.convertJavaDateToSqlDate(date);
                }
                if (columnType.equals(SQLDataTypes.DATETIME) || columnType.equals(SQLDataTypes.TIME_STAMP)) {
                    Date date = (Date) value;
                    value = ConvertHelper.convertJavaDateToSqlDateTime(date);
                }
                //id checker
                if (field.isAnnotationPresent(Id.class)) {
                    Id currentId = (Id) field.getAnnotation(Id.class);
                    if (currentId.autoIncrement()) {
                        continue;
                    }
                }
                if (value == null) {
                    //append null
                    stringCmd.append(SQLConstant.NULL);
                    stringCmd.append(SQLConstant.COMMON);
                    stringCmd.append(SQLConstant.SPACE);
                    continue;
                }
                if (SQLDataTypes.needApostrophe(columnType)) {
                    stringCmd.append(SQLConstant.QUOTE);
                }
                stringCmd.append(value);
                if (SQLDataTypes.needApostrophe(columnType)) {
                    stringCmd.append(SQLConstant.QUOTE);
                }
                stringCmd.append(SQLConstant.COMMON);
                stringCmd.append(SQLConstant.SPACE);

            }
            stringCmd.setLength(stringCmd.length() - 2);
            stringCmd.append(SQLConstant.CLOSE_PARENTHESES);
            System.out.println(stringCmd.toString());
            return ConnectionHelper.getConnection().createStatement().execute(stringCmd.toString());
        } catch (IllegalAccessException | MyModelError | SQLException e) {
            System.err.printf("Save Model Error: %s.\n", e.getMessage());
        }
        return false;
    }

    public T findById(Object id) {
        try {
            if (!isEntity()) {
                throw new MyModelError("Not an entity model check your annotation");
            }
            //select * from teachers where id = {id}
            //build sql cmd
            StringBuilder strCmd = new StringBuilder();
            strCmd.append(SQLConstant.SELECT_ASTERISK);
            strCmd.append(SQLConstant.SPACE);
            strCmd.append(SQLConstant.FROM);
            strCmd.append(SQLConstant.SPACE);
            //get table name
            Entity currentEntity = clazz.getDeclaredAnnotation(Entity.class);
            String tableName = currentEntity.tableName();
            strCmd.append(tableName);
            strCmd.append(SQLConstant.SPACE);
            strCmd.append(SQLConstant.WHERE);
            strCmd.append(SQLConstant.SPACE);
            //get id column name
            for (Field field : clazz.getDeclaredFields()) {
                //skip non column
                if (!field.isAnnotationPresent(Column.class)) {
                    continue;
                }

                if (field.isAnnotationPresent(Id.class)) {
                    Column column = field.getAnnotation(Column.class);
                    strCmd.append(column.columnName());
                    strCmd.append(SQLConstant.SPACE);
                    strCmd.append(SQLConstant.EQUAL);
                    strCmd.append(SQLConstant.SPACE);
                    //append value depends on value type
                    if (!column.columnType().equals(SQLDataTypes.INTEGER)) {
                        strCmd.append(SQLConstant.QUOTE);
                    }
                    strCmd.append(id);
                    if (!column.columnType().equals(SQLDataTypes.INTEGER)) {
                        strCmd.append(SQLConstant.QUOTE);
                    }
                    break;
                }
            }
            System.out.println(strCmd.toString());
            PreparedStatement preparedStatement = ConnectionHelper.getConnection().prepareStatement(strCmd.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) { // trỏ đến các bản ghi cho đến khi trả về false.
                T obj = clazz.newInstance(); // khởi tạo ra đối tượng cụ thể của class T.
                for (Field field : clazz.getDeclaredFields()) {
                    if (!field.isAnnotationPresent(Column.class)) {
                        continue;
                    }
                    field.setAccessible(true);
                    Column columnInfor = field.getAnnotation(Column.class);
                    switch (columnInfor.columnType()) {
                        case SQLDataTypes.INTEGER:
                            // set giá trị của trường đó cho đối tượng mới tạo ở trên.
                            field.set(obj, resultSet.getInt(columnInfor.columnName()));
                            break;
                        case SQLDataTypes.VARCHAR255:
                        case SQLDataTypes.VARCHAR50:
                        case SQLDataTypes.TEXT:
                            field.set(obj, resultSet.getString(columnInfor.columnName()));
                            break;
                        case SQLDataTypes.DOUBLE:
                            field.set(obj, resultSet.getDouble(columnInfor.columnName()));
                            break;
                        case SQLDataTypes.DATE:
                            field.set(obj, resultSet.getDate(columnInfor.columnName()));
                            break;
                        case SQLDataTypes.DATETIME:
                        case SQLDataTypes.TIME_STAMP:
                            field.set(obj, ConvertHelper.convertSqlTimeStampToJavaDate(resultSet.getTimestamp(columnInfor.columnName())));
                            break;
                    }
                }
                return obj;
            }
        } catch (MyModelError | SQLException | InstantiationException | IllegalAccessException error) {
            System.out.printf("Find by Id model error: %s \n", error.getMessage());
        }
        return null;
    }

    public List<T> findAll(int a, int b) {
        List<T> result = new ArrayList<>();
        Entity entityInfor = clazz.getAnnotation(Entity.class);
        StringBuilder stringQuery = new StringBuilder();
        stringQuery.append(SQLConstant.SELECT_ASTERISK);
        stringQuery.append(SQLConstant.SPACE);
        stringQuery.append(SQLConstant.FROM);
        stringQuery.append(SQLConstant.SPACE);
        stringQuery.append(entityInfor.tableName());
        stringQuery.append(SQLConstant.SPACE);
        stringQuery.append(SQLConstant.ORDER_BY);
        stringQuery.append(SQLConstant.SPACE);
        for (Field field1 : clazz.getDeclaredFields()) {
            if (!field1.isAnnotationPresent(Column.class)) {
                continue;
            }
            field1.setAccessible(true);
            Column columnInfor = field1.getAnnotation(Column.class);
            if (columnInfor.columnName().equals("id")) {
                stringQuery.append(columnInfor.columnName());
                stringQuery.append(SQLConstant.SPACE); //
                stringQuery.append(SQLConstant.DESC); //
            }
        }
        stringQuery.append(SQLConstant.SPACE);
        stringQuery.append(SQLConstant.LIMIT);
        stringQuery.append(SQLConstant.SPACE);
        stringQuery.append(SQLConstant.MARK_QUESTION);
        stringQuery.append(SQLConstant.COMMON);
        stringQuery.append(SQLConstant.MARK_QUESTION);
        System.out.println(stringQuery.toString());
        try {
            PreparedStatement preparedStatement = ConnectionHelper.getConnection().prepareStatement(stringQuery.toString());
            preparedStatement.setInt(1, a);
            preparedStatement.setInt(2, b);
            // thực thi câu lệnh select * from.
            // trả về ResultSet (nó thêm thằng con trỏ)
            ResultSet resultSet = preparedStatement.executeQuery();
            Field[] fields = clazz.getDeclaredFields(); //
            while (resultSet.next()) { // trỏ đến các bản ghi cho đến khi trả về false.
                T obj = clazz.newInstance(); // khởi tạo ra đối tượng cụ thể của class T.
                for (Field field : clazz.getDeclaredFields()) {
                    if (!field.isAnnotationPresent(Column.class)) {
                        continue;
                    }
                    field.setAccessible(true);
                    Column columnInfor = field.getAnnotation(Column.class);
                    switch (columnInfor.columnType()) {
                        case SQLDataTypes.INTEGER:
                            // set giá trị của trường đó cho đối tượng mới tạo ở trên.
                            field.set(obj, resultSet.getInt(columnInfor.columnName()));
                            break;
                        case SQLDataTypes.VARCHAR255:
                        case SQLDataTypes.VARCHAR50:
                        case SQLDataTypes.TEXT:
                            field.set(obj, resultSet.getString(columnInfor.columnName()));
                            break;
                        case SQLDataTypes.DOUBLE:
                            field.set(obj, resultSet.getDouble(columnInfor.columnName()));
                            break;
                        case SQLDataTypes.DATE:
                            field.set(obj, resultSet.getDate(columnInfor.columnName()));
                            break;
                        case SQLDataTypes.DATETIME:
                        case SQLDataTypes.TIME_STAMP:
                            field.set(obj, ConvertHelper.convertSqlTimeStampToJavaDate(resultSet.getTimestamp(columnInfor.columnName())));
                            break;
                    }
                }
                System.out.println(stringQuery.toString());
                result.add(obj);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            System.err.printf("Có lỗi xảy ra trong quá trình làm việc với database. Error %s.\n", e.getMessage());
        }
        return result;
    }
    //Total product number
    public int getCountCategory() {
        ArrayList<T> list = new ArrayList();
        String stringQuery = "SELECT count(id) FROM categories WHERE STATUS = 1";
        int count = 0;
        try {
            PreparedStatement preparedStatement = ConnectionHelper.getConnection().prepareStatement(stringQuery.toString());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    public int getCountFood() {
        ArrayList<T> list = new ArrayList();
        String stringQuery = "SELECT count(id) FROM foods WHERE STATUS = 1";
        int count = 0;
        try {
            PreparedStatement preparedStatement = ConnectionHelper.getConnection().prepareStatement(stringQuery.toString());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    public List<T> findByCondition(String stringCmd) {
        List<T> listObj = new ArrayList<T>();
        try {
            if (!isEntity()) {
                throw new MyModelError("Not an entity class");
            }
            System.out.println(stringCmd);
            PreparedStatement preparedStatement = ConnectionHelper.getConnection().prepareStatement(stringCmd.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                T obj = clazz.newInstance();
                for (Field field : clazz.getDeclaredFields()) {
                    if (!field.isAnnotationPresent(Column.class)) {
                        continue;
                    }
                    field.setAccessible(true);
                    Column columnInfor = field.getAnnotation(Column.class);

                    switch (columnInfor.columnType()) {
                        case SQLDataTypes.INTEGER:
                            // set giá trị của trường đó cho đối tượng mới tạo ở trên.
                            field.set(obj, resultSet.getInt(columnInfor.columnName()));
                            break;
                        case SQLDataTypes.VARCHAR255:
                        case SQLDataTypes.VARCHAR50:
                            field.set(obj, resultSet.getString(columnInfor.columnName()));
                            break;
                        case SQLDataTypes.DOUBLE:
                            field.set(obj, resultSet.getDouble(columnInfor.columnName()));
                            break;
                        case SQLDataTypes.DATE:
                            field.set(obj, resultSet.getDate(columnInfor.columnName()));
                            break;
                        case SQLDataTypes.DATETIME:
                        case SQLDataTypes.TIME_STAMP:
                            field.set(obj, ConvertHelper.convertSqlTimeStampToJavaDate(resultSet.getTimestamp(columnInfor.columnName())));
                            break;
                    }
                }
                listObj.add(obj);
            }
        } catch (MyModelError | InstantiationException | IllegalAccessException | SQLException error) {
            System.err.printf("Find all error %s\n", error.getMessage());
            Logger.getLogger("Hello bug").log(Level.SEVERE, error.getMessage());
        }
        return listObj;
    }

    public boolean update(String id, T obj) {
        // Lấy ra giá trị của annotation @Entity vì cần những thông tin liên quan đến tableName.
        Entity entityInfor = clazz.getAnnotation(Entity.class);
        // Build lên câu query string.
        StringBuilder strQuery = new StringBuilder();
        StringBuilder fieldValues = new StringBuilder();

        // Xây dựng câu lệnh update theo tên bảng, theo tên các field cùa đối tượng truyền vào.
        strQuery.append(SQLConstant.UPDATE); // insert into
        strQuery.append(SQLConstant.SPACE); //
        strQuery.append(entityInfor.tableName()); // giangvien
        strQuery.append(SQLConstant.SPACE); //
        strQuery.append(SQLConstant.SET); //
        strQuery.append(SQLConstant.SPACE); //
        for (Field field : clazz.getDeclaredFields()) {
            // check xem trường có phải là @Column không.
            if (!field.isAnnotationPresent(Column.class)) {
                // bỏ qua trong trường hợp không được đánh là @Column.
                continue;
            }
            // cần set bằng true để có thể set, get giá trị của field trong một object nào đó.
            field.setAccessible(true);
            // lấy thông tin column để check tên trường, kiểu giá trị của trường.
            // Không lấy danh sách column theo tên field mà lấy theo annotation đặt tại field đó.
            Column columnInfor = field.getAnnotation(Column.class);
            // check xem trường có phải là id không.
            if (!field.isAnnotationPresent(Id.class)) {
                strQuery.append(columnInfor.columnName()); // nối tên trường.
                strQuery.append(SQLConstant.SPACE); //
                strQuery.append(SQLConstant.EQUAL); //,
                strQuery.append(SQLConstant.SPACE); //
                // nhanh trí, xử lý luôn phần value, tránh sử dụng 2 vòng lặp.
                // check kiểu của trường, nếu là string thì thêm dấu '
                if (field.getType().getSimpleName().equals(String.class.getSimpleName())||
                        field.getType().getSimpleName().equals(Date.class.getSimpleName())) {
                    strQuery.append(SQLConstant.QUOTE);
                }
                try {
                    strQuery.append(field.get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
//                    fieldValues.append(field.get(obj)); // field.setAccessible(true);
                // check kiểu của trường, nếu là string thì thêm dấu '
                if (field.getType().getSimpleName().equals(String.class.getSimpleName())||
                        field.getType().getSimpleName().equals(Date.class.getSimpleName())) {
                    strQuery.append(SQLConstant.QUOTE);
                }
                strQuery.append(SQLConstant.COMMON); // nối giá trị các trường vào.
                strQuery.append(SQLConstant.SPACE); // nối giá trị các trường vào.
            }
        }
        strQuery.setLength(strQuery.length() - 2);
        strQuery.append(SQLConstant.SPACE);
        strQuery.append(SQLConstant.WHERE); // nối giá trị các trường vào.
        strQuery.append(SQLConstant.SPACE);
        for (Field field1 : clazz.getDeclaredFields()) {
            // check xem trường có phải là @Column không.
            if (!field1.isAnnotationPresent(Column.class)) {
                // bỏ qua trong trường hợp không được đánh là @Column.
                continue;
            }
            // cần set bằng true để có thể set, get giá trị của field trong một object nào đó.
            field1.setAccessible(true);

            Column columnInfor = field1.getAnnotation(Column.class);
            if (field1.isAnnotationPresent(Id.class)) {
                // lấy thông tin id.
                strQuery.append(columnInfor.columnName()); // nối tên trường.
                strQuery.append(SQLConstant.SPACE); //
                strQuery.append(SQLConstant.EQUAL); //
                strQuery.append(SQLConstant.SPACE); //
                // nhanh trí, xử lý luôn phần value, tránh sử dụng 2 vòng lặp.
                // check kiểu của trường, nếu là string thì thêm dấu '
                if (field1.getType().getSimpleName().equals(String.class.getSimpleName())||
                        field1.getType().getSimpleName().equals(Date.class.getSimpleName())) {
                    fieldValues.append(SQLConstant.QUOTE);
                }
                // lấy ra thông tin giá trị của trường đó tại obj truyền vào.
                fieldValues.append(id); // field.setAccessible(true);
                // check kiểu của trường, nếu là string thì thêm dấu '
                if (field1.getType().getSimpleName().equals(String.class.getSimpleName())||
                        field1.getType().getSimpleName().equals(Date.class.getSimpleName())) {
                    fieldValues.append(SQLConstant.QUOTE);
                }
                fieldValues.append(SQLConstant.SPACE); //
                strQuery.append(fieldValues); //
            }
        }
        System.out.println("lệnh update \n");
        System.out.println(strQuery.toString());
        try {
            ConnectionHelper.getConnection().createStatement().execute(strQuery.toString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return obj != null;
    }

    public boolean delete(Object id) {
        //delete from {tableName} where id = id
        try {
            if (!isEntity()) {
                throw new MyModelError("Not an entity model check your annotation");
            }
            String tableName = clazz.getAnnotation(Entity.class).tableName();
            StringBuilder stringCmd = new StringBuilder();
            stringCmd.append(SQLConstant.DELETE);
            stringCmd.append(SQLConstant.SPACE);
            stringCmd.append(SQLConstant.FROM);
            stringCmd.append(SQLConstant.SPACE);
            stringCmd.append(tableName);
            stringCmd.append(SQLConstant.SPACE);
            stringCmd.append(SQLConstant.WHERE);
            //id information
            String idName = "";
            String idType = "";
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Column.class)) {
                    continue;
                }

                field.setAccessible(true);
                Column columnInformation = field.getDeclaredAnnotation(Column.class);
                String columnName = columnInformation.columnName();
                String columnType = columnInformation.columnType();
                if (field.isAnnotationPresent(Id.class)) {
                    //dont update id
                    //but get id information
                    idName = columnName;
                    idType = columnType;
                    break;
                }
            }
            stringCmd.append(SQLConstant.SPACE);
            stringCmd.append(idName);
            stringCmd.append(SQLConstant.SPACE);
            stringCmd.append(SQLConstant.EQUAL);
            stringCmd.append(SQLConstant.SPACE);
            if (!idType.equals(SQLDataTypes.INTEGER)) {
                stringCmd.append(SQLConstant.QUOTE);
            }
            stringCmd.append(id);
            if (!idType.equals(SQLDataTypes.INTEGER)) {
                stringCmd.append(SQLConstant.QUOTE);
            }
            ConnectionHelper.getConnection().createStatement().execute(stringCmd.toString());
            return true;
        } catch (MyModelError | SQLException error) {
            System.out.printf("Delete failed  error: %s \n", error.getMessage());
        }
        return false;
    }
}
