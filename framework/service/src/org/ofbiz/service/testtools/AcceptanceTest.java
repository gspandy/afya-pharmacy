package org.ofbiz.service.testtools;

import com.github.springtestdbunit.assertion.DatabaseAssertion;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseDataSet;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.transaction.TransactionUtil;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Author: Nthdimenzion
 */

public class AcceptanceTest extends OFBizTestCase {
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1/smehrms?createDatabaseIfNotExist=true&amp;characterEncoding=UTF-8&amp;characterSetResults=UTF-8";
    private static final String USER = "root";
    private static final java.lang.String PASSWORD = "welcome";

    protected GenericValue userLogin = null;
    IDatabaseTester databaseTester;

    public AcceptanceTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        TransactionUtil.begin();
        userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system"));
        databaseTester = new JdbcDatabaseTester(JDBC_DRIVER, JDBC_URL, USER, PASSWORD);
    }

    @Override
    protected void tearDown() throws Exception {
        TransactionUtil.rollback(new RuntimeException());
        databaseTester.setTearDownOperation(DatabaseOperation.DELETE);
        databaseTester.onTearDown();
    }

    protected final void testSetUp(String setupDataSetFile){
        final IDataSet setupDataSet;
        try {
            setupDataSet = new FlatXmlDataSetBuilder().build(FileUtil.getFile(setupDataSetFile));
            databaseTester.setDataSet(setupDataSet);
            databaseTester.setSetUpOperation(DatabaseOperation.INSERT);
            databaseTester.onSetup();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected final void verifyOutcome(String expectedDataSetFile) {
        IDatabaseConnection connection = null;
        try {
            final IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(FileUtil.getFile(expectedDataSetFile));
            connection = databaseTester.getConnection();
            final IDataSet actualDataSet = new DatabaseDataSet(connection, false);
            DatabaseAssertion databaseAssertion = DatabaseAssertionMode.NON_STRICT.getDatabaseAssertion();
            databaseAssertion.assertEquals(expectedDataSet, actualDataSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}
