package br.edu.unijui.pcn.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBManager {

    private final String hostName;
    private final int port;
    private final String dbName;
    private final String username;
    private final String password;

    public DBManager(String hostName, int port, String dbName, String username, String password) {
        this.hostName = hostName;
        this.port = port;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
    }

   private Connection openConnection() throws SQLException {
    String url = String.format("jdbc:derby://%s:%d/%s", hostName, port, dbName);
    
    try {
        Class.forName("org.apache.derby.client.ClientAutoloadedDriver");
    } catch (ClassNotFoundException e) {
        throw new SQLException("Driver Derby não encontrado no classpath", e);
    }
    
    return DriverManager.getConnection(url, username, password);
}

public void insertAll(List<IsolationRecord> records, boolean enableTransactions) throws SQLException {

    Connection conn = openConnection();
    try {
        if (enableTransactions == true) {
            conn.setAutoCommit(false);
        }

        Map<String, Long> stateCache = new HashMap<>();

        String sqlInsertIsolation = "INSERT INTO SOCIAL_ISOLATION (CITY, STATE_ID, \"INDEX\", DATE_WHEN) VALUES (?, ?, ?, ?)";
        try (PreparedStatement psmtIso = conn.prepareStatement(sqlInsertIsolation)) {
            for (IsolationRecord record : records) {
                String sigla = record.stateAcronym();
                Long stateId;
                if (stateCache.containsKey(sigla)) {
                    stateId = stateCache.get(sigla);
                } else {
                    stateId = getOrInsertState(conn, record);
                    stateCache.put(sigla, stateId);
                }
                
                psmtIso.setString  (1, record.city());
                psmtIso.setLong    (2, stateId);
                psmtIso.setDouble  (3, record.index());
                psmtIso.setString  (4, record.date());
                
                psmtIso.executeUpdate();
            }
        }

        if (enableTransactions == true) {
            conn.commit();
        }

    } catch (SQLException e) {
        if (enableTransactions == true) {
            conn.rollback();
        }
        throw e;
    } finally {
        conn.close();
    }
}
    
    private Long getOrInsertState(Connection conn, IsolationRecord record) throws SQLException {
        String sqlGet = "SELECT ID FROM STATE WHERE NAME = ?";
        String sqlInsert = "INSERT INTO STATE (NAME, ACRONYM) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sqlGet)) {
            pstmt.setString(1, record.state());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("ID");
                }
            }
        }
        try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmtInsert.setString(1, record.state());
            pstmtInsert.setString(2, record.stateAcronym());
            pstmtInsert.executeUpdate();

            try (ResultSet generatedKeys = pstmtInsert.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        }
        return 0L;
    }
    
    public IsolationRecord findTheHighest(String whereToFind) {
       
        try (Connection conn = openConnection()) {
           
            if (whereToFind.equals("Brazil")) {
                String sql = """
                  SELECT s.NAME,
                         s.ACRONYM,
                         si.CITY,
                         si."INDEX",
                         si.DATE_WHEN
                  FROM SOCIAL_ISOLATION si
                  JOIN STATE s ON s.ID = si.STATE_ID
                  ORDER BY si."INDEX" DESC
                  FETCH FIRST 1 ROW ONLY
                  """;

                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new IsolationRecord(
                            rs.getString("NAME"),
                            rs.getString("ACRONYM"),
                            rs.getString("CITY"),
                            rs.getDouble("INDEX"),
                            rs.getString("DATE_WHEN")
                    );
                }
            } else {
                String acronym = whereToFind.substring( 
                    whereToFind.indexOf("(") + 1,
                    whereToFind.indexOf(")")
                );

                String sql = """
                            SELECT 
                                s.NAME,
                                s.ACRONYM,
                                si.CITY,
                                si."INDEX",
                                si.DATE_WHEN
                            FROM SOCIAL_ISOLATION si
                            JOIN STATE s ON s.ID = si.STATE_ID
                            WHERE s.ACRONYM = ?
                            ORDER BY si."INDEX" DESC
                            FETCH FIRST 1 ROW ONLY      
                            """;
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, acronym);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new IsolationRecord(
                            rs.getString("NAME"),
                            rs.getString("ACRONYM"),
                            rs.getString("CITY"),
                            rs.getDouble("INDEX"),
                            rs.getString("DATE_WHEN")
                    );
                }
            }
        } catch (Exception e) {  
            e.printStackTrace();
        }
        return null;
    }
    
    public IsolationRecord findTheLowest(String whereToFind) {
       try (Connection conn = openConnection()) {
           
            if (whereToFind.equals("Brazil")) {
                String sql = """
                  SELECT s.NAME,
                         s.ACRONYM,
                         si.CITY,
                         si."INDEX" AS ISOLATION_INDEX,
                         si.DATE_WHEN
                  FROM SOCIAL_ISOLATION si
                  JOIN STATE s ON s.ID = si.STATE_ID
                  ORDER BY si."INDEX" ASC
                  FETCH FIRST 1 ROW ONLY
                  """;

                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new IsolationRecord(
                            rs.getString("NAME"),
                            rs.getString("ACRONYM"),
                            rs.getString("CITY"),
                            rs.getDouble("ISOLATION_INDEX"),
                            rs.getString("DATE_WHEN")
                    );
                }
            } else {
                String acronym = whereToFind.substring( 
                    whereToFind.indexOf("(") + 1,
                    whereToFind.indexOf(")")
                );

                String sql = """
                            SELECT s.NAME,
                                s.ACRONYM,
                                si.CITY,
                                si."INDEX" AS ISOLATION_INDEX,
                                si.DATE_WHEN
                            FROM SOCIAL_ISOLATION si
                            JOIN STATE s ON s.ID = si.STATE_ID
                            WHERE s.ACRONYM = ?
                            ORDER BY si."INDEX" ASC
                            FETCH FIRST 1 ROW ONLY      
                            """;
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, acronym);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new IsolationRecord(
                            rs.getString("NAME"),
                            rs.getString("ACRONYM"),
                            rs.getString("CITY"),
                            rs.getDouble("ISOLATION_INDEX"),
                            rs.getString("DATE_WHEN")
                    );
                }
            }
        } catch (Exception e) {  
            e.printStackTrace();
        }
        return null;
    }
}
