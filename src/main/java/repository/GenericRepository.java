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
            while (resultSet.next()) { // tr??? ?????n c??c b???n ghi cho ?????n khi tr??? v??? false.
                T obj = clazz.newInstance(); // kh???i t???o ra ?????i t?????ng c??? th??? c???a class T.
                for (Field field : clazz.getDeclaredFields()) {
                    if (!field.isAnnotationPresent(Column.class)) {
                        continue;
                    }
                    field.setAccessible(true);
                    Column columnInfor = field.getAnnotation(Column.class);
                    switch (columnInfor.columnType()) {
                        case SQLDataTypes.INTEGER:
                            // set gi?? tr??? c???a tr?????ng ???? cho ?????i t?????ng m???i t???o ??? tr??n.
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
            // th???c thi c??u l???nh select * from.
            // tr??? v??? ResultSet (n?? th??m th???ng con tr???)
            ResultSet resultSet = preparedStatement.executeQuery();
            Field[] fields = clazz.getDeclaredFields(); //
            while (resultSet.next()) { // tr??? ?????n c??c b???n ghi cho ?????n khi tr??? v??? false.
                T obj = clazz.newInstance(); // kh???i t???o ra ?????i t?????ng c??? th??? c???a class T.
                for (Field field : clazz.getDeclaredFields()) {
                    if (!field.isAnnotationPresent(Column.class)) {
                        continue;
                    }
                    field.setAccessible(true);
                    Column columnInfor = field.getAnnotation(Column.class);
                    switch (columnInfor.columnType()) {
                        case SQLDataTypes.INTEGER:
                            // set gi?? tr??? c???a tr?????ng ???? cho ?????i t?????ng m???i t???o ??? tr??n.
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
            System.err.printf("C?? l???i x???y ra trong qu?? tr??nh l??m vi???c v???i database. Error %s.\n", e.getMessage());
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
                            // set gi?? tr??? c???a tr?????ng ???? cho ?????i t?????ng m???i t???o ??? tr??n.
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
        // L???y ra gi?? tr??? c???a annotation @Entity v?? c???n nh???ng th??ng tin li??n quan ?????n tableName.
        Entity entityInfor = clazz.getAnnotation(Entity.class);
        // Build l??n c??u query string.
        StringBuilder strQuery = new StringBuilder();
        StringBuilder fieldValues = new StringBuilder();

        // X??y d???ng c??u l???nh update theo t??n b???ng, theo t??n c??c field c??a ?????i t?????ng truy???n v??o.
        strQuery.append(SQLConstant.UPDATE); // insert into
        strQuery.append(SQLConstant.SPACE); //
        strQuery.append(entityInfor.tableName()); // giangvien
        strQuery.append(SQLConstant.SPACE); //
        strQuery.append(SQLConstant.SET); //
        strQuery.append(SQLConstant.SPACE); //
        for (Field field : clazz.getDeclaredFields()) {
            // check xem tr?????ng c?? ph???i l?? @Column kh??ng.
            if (!field.isAnnotationPresent(Column.class)) {
                // b??? qua trong tr?????ng h???p kh??ng ???????c ????nh l?? @Column.
                continue;
            }
            // c???n set b???ng true ????? c?? th??? set, get gi?? tr??? c???a field trong m???t object n??o ????.
            field.setAccessible(true);
            // l???y th??ng tin column ????? check t??n tr?????ng, ki???u gi?? tr??? c???a tr?????ng.
            // Kh??ng l???y danh s??ch column theo t??n field m?? l???y theo annotation ?????t t???i field ????.
            Column columnInfor = field.getAnnotation(Column.class);
            // check xem tr?????ng c?? ph???i l?? id kh??ng.
            if (!field.isAnnotationPresent(Id.class)) {
                strQuery.append(columnInfor.columnName()); // n???i t??n tr?????ng.
                strQuery.append(SQLConstant.SPACE); //
                strQuery.append(SQLConstant.EQUAL); //,
                strQuery.append(SQLConstant.SPACE); //
                // nhanh tr??, x??? l?? lu??n ph???n value, tr??nh s??? d???ng 2 v??ng l???p.
                // check ki???u c???a tr?????ng, n???u l?? string th?? th??m d???u '
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
                // check ki???u c???a tr?????ng, n???u l?? string th?? th??m d???u '
                if (field.getType().getSimpleName().equals(String.class.getSimpleName())||
                        field.getType().getSimpleName().equals(Date.class.getSimpleName())) {
                    strQuery.append(SQLConstant.QUOTE);
                }
                strQuery.append(SQLConstant.COMMON); // n???i gi?? tr??? c??c tr?????ng v??o.
                strQuery.append(SQLConstant.SPACE); // n???i gi?? tr??? c??c tr?????ng v??o.
            }
        }
        strQuery.setLength(strQuery.length() - 2);
        strQuery.append(SQLConstant.SPACE);
        strQuery.append(SQLConstant.WHERE); // n???i gi?? tr??? c??c tr?????ng v??o.
        strQuery.append(SQLConstant.SPACE);
        for (Field field1 : clazz.getDeclaredFields()) {
            // check xem tr?????ng c?? ph???i l?? @Column kh??ng.
            if (!field1.isAnnotationPresent(Column.class)) {
                // b??? qua trong tr?????ng h???p kh??ng ???????c ????nh l?? @Column.
                continue;
            }
            // c???n set b???ng true ????? c?? th??? set, get gi?? tr??? c???a field trong m???t object n??o ????.
            field1.setAccessible(true);

            Column columnInfor = field1.getAnnotation(Column.class);
            if (field1.isAnnotationPresent(Id.class)) {
                // l???y th??ng tin id.
                strQuery.append(columnInfor.columnName()); // n???i t??n tr?????ng.
                strQuery.append(SQLConstant.SPACE); //
                strQuery.append(SQLConstant.EQUAL); //
                strQuery.append(SQLConstant.SPACE); //
                // nhanh tr??, x??? l?? lu??n ph???n value, tr??nh s??? d???ng 2 v??ng l???p.
                // check ki???u c???a tr?????ng, n???u l?? string th?? th??m d???u '
                if (field1.getType().getSimpleName().equals(String.class.getSimpleName())||
                        field1.getType().getSimpleName().equals(Date.class.getSimpleName())) {
                    fieldValues.append(SQLConstant.QUOTE);
                }
                // l???y ra th??ng tin gi?? tr??? c???a tr?????ng ???? t???i obj truy???n v??o.
                fieldValues.append(id); // field.setAccessible(true);
                // check ki???u c???a tr?????ng, n???u l?? string th?? th??m d???u '
                if (field1.getType().getSimpleName().equals(String.class.getSimpleName())||
                        field1.getType().getSimpleName().equals(Date.class.getSimpleName())) {
                    fieldValues.append(SQLConstant.QUOTE);
                }
                fieldValues.append(SQLConstant.SPACE); //
                strQuery.append(fieldValues); //
            }
        }
        System.out.println("l????nh update \n");
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
