import java.sql.*;
import java.util.ArrayList;

public class Main {

    private static ArrayList<Goods> goods;

    private static String TABLE_NAME = "store";

    private static int AMOUNT_GOODS = 3;

    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {

        goods = instantGods(AMOUNT_GOODS);

        try {

            connect();
            System.out.println("Connected");


            addTable();
            System.out.println("Table added");

            addRows(goods);
            System.out.println("Rows added");


            System.out.println("Show all DB: ");
            showDB();

            System.out.println("Update row in DB by name : name1");;
            updateRow(new Goods("name1", 1000));

            System.out.println("Show changes DB: ");
            showDB();

            System.out.println("Delete row by name : name2");
            delRow("name2");

            System.out.println("Show all DB after deleting: ");
            showDB();

            delTable();
            System.out.println("Table deleted");

            disconnect();
            System.out.println("Disconnected");

        } catch (Exception exc){
            exc.printStackTrace();
        }

    }

    private static void showDB() throws Exception {
        ArrayList<Goods> goodsInDB = getRows();
        for (int i = 0; i < goodsInDB.size(); i++) {
            Goods temp = goodsInDB.get(i);
            System.out.println(String.format("product %s : %d",temp.getName(), temp.amount));
        }
    }

    private static ArrayList<Goods> instantGods(int amountGoods) {

        ArrayList<Goods> result = new ArrayList<>();

        for (int i = 0; i < amountGoods; i++) {
            result.add(new Goods("name" + i, i * 5));
        }

        return result;
    }

    private static void connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:sqlitetest.db");
        statement = connection.createStatement();
    }

    private static void disconnect() throws Exception{
        connection.close();
    }

    private static void addTable() throws Exception {

        statement.execute(String.format("CREATE TABLE IF NOT EXISTS %s(\nid integer PRIMARY KEY,\nname TEXT NOT NULL,\namount integer NOT NULL\n);", TABLE_NAME));
    }

    public static void addRows(ArrayList<Goods> goods) throws Exception{

        connection.setAutoCommit(false);

        for (Goods g : goods) {
            PreparedStatement prsmt = connection.prepareStatement(String.format("INSERT INTO %s (name, amount) VALUES (?,?)", TABLE_NAME));
            prsmt.setString(1, g.getName());
            prsmt.setInt(2, g.amount);
            prsmt.executeUpdate();
        }

        connection.commit();
        connection.setAutoCommit(true);
    }

    private static ArrayList<Goods> getRows() throws Exception{
        ArrayList<Goods> goods = new ArrayList<>();

        ResultSet rs = statement.executeQuery(String.format("SELECT id, name, amount FROM %s", TABLE_NAME));
        while (rs.next()) {
            goods.add(new Goods(rs.getString("name"), rs.getInt("amount")));
        }
        return goods;
    }

    private static void updateRow(Goods goods) throws Exception{

        statement.execute(String.format("UPDATE %s SET amount = %d WHERE name = '%s'", TABLE_NAME, goods.amount, goods.getName()));

    }

    public static void updateRows(ArrayList<Goods> goods) throws Exception {
        connection.setAutoCommit(false);
        for(Goods g : goods) {
            updateRow(g);
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    public static void delRow(String name) throws Exception{
        statement.executeUpdate(String.format("DELETE FROM %s WHERE name = '%s'", TABLE_NAME, name));
    }

    public static void delTable() throws Exception{
        statement.execute(String.format("DROP TABLE %s", TABLE_NAME));
    }

}
