package dao;

import models.Squad;
import models.Hero;
import org.junit.*;
import org.sql2o.*;

import static junit.framework.TestCase.assertEquals;
import static  junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;



public class Sql2oSquadDaoTest {
    private static Sql2oSquadDao squadDao;  //static so that they can manipulate whole class
    private static Sql2oHeroDao heroDao;
    private static Connection conn;

    @BeforeClass                                        //changed to @BeforeClass (run once before running any tests in this file)
    public static void setUp() throws Exception {
        String connectionString = "jdbc:postgresql://localhost:5432/herosquad_test";    //connect to postgresql test db
        Sql2o sql2o = new Sql2o(connectionString, "postgres", "password");
//        String connectionString = "jdbc:postgresql://ec2-54-197-48-79.compute-1.amazonaws.com:5432/dd9atnupft6c3p";
//        Sql2o sql2o = new Sql2o(connectionString, "wwrapikqmdhwkt", "9ef20a089add951f7b577926fce905a990551560008cd2e89f96a41ade30c47c");
        squadDao = new Sql2oSquadDao(sql2o);
        heroDao = new Sql2oHeroDao(sql2o);
        conn = sql2o.open();                            //open connection once before this test is run
    }

    @After                                              //run after every test
    public void tearDown() throws Exception {
        System.out.println("clearing database");
        squadDao.clearAllSquads();                      //clear all squads after every test
        heroDao.clearAllHeroes();                       //clear all heroes after every test
    }

    @AfterClass                                         //run once after every test in this test file are completed
    public static void shutdown() throws Exception{
        conn.close();                                   //close connection once after all tests in this test file are done
        System.out.println("connection closed");
    }

    @Test
    public void addingSquadSetsId()throws Exception{
        Squad squad = setupNewSquad();
        int originalSquadId = squad.getId();
        squadDao.add(squad);
        assertNotEquals(originalSquadId, squad.getId());
    }

    @Test
    public void existingSquadCanBeFoundById() throws Exception{
        Squad squad = setupNewSquad();
        squadDao.add(squad);
        Squad foundSquad = squadDao.findById(squad.getId());
        assertEquals(squad, foundSquad);
    }

    @Test
    public void addedSquadsAreReturnedFromGetAll()throws Exception{
        Squad squad = setupNewSquad();
        squadDao.add(squad);
        assertEquals(1, squadDao.getAll().size());
    }

//    @Test
//    public void noSquadReturnsEmptyList()throws Exception{
//        assertEquals(0, squadDao.getAll().size());
//    }

//    @Test
//    public void updateChangesSquadContent()throws Exception{
//        String initialSquadName = "justice league";
//        Squad squad = setupNewSquad();
//        squadDao.add(squad);
//        squadDao.update(squad.getId(), "avengers", "Killing Thanos", 3);
//        Squad updatedSquad = squadDao.findById(squad.getId());
//        assertNotEquals(initialSquadName, updatedSquad.getSquadName());
//    }

    @Test
    public void deleteByIdDeletesCorrectSquad()throws Exception{
        Squad squad = setupNewSquad();
        squadDao.add(squad);
        squadDao.deleteById(squad.getId());
        assertEquals(0, squadDao.getAll().size());
    }

    @Test
    public void clearAllClearsAllSquads()throws Exception{
        Squad squad = setupNewSquad();
        Squad anotherSquad = new Squad("power rangers", "fighting all evil", 5);
        squadDao.add(squad);
        squadDao.add(anotherSquad);
        int daoSize = squadDao.getAll().size();
        squadDao.clearAllSquads();
        assertTrue(daoSize > 0 && daoSize > squadDao.getAll().size());
    }

//    @Test
//    public void getAllHeroesBySquadReturnsAllHeroesCorrectly()throws Exception{
//        Squad squad = setupNewSquad();
//        squadDao.add(squad);
//        int squadId = squad.getId();
//        Hero newHero = new Hero("flash", "fatigue", "speed", 25, squadId);
//        Hero newHero1 = new Hero("batman", "women", "money", 24, squadId);
//        Hero newHero2 = new Hero("superman", "cryptonite", "laser eyes", 30, squadId);
//        heroDao.add(newHero);
//        heroDao.add(newHero1);
//        assertEquals(2, squadDao.getAllHeroesBySquad(squadId).size());
//        assertTrue(squadDao.getAllHeroesBySquad(squadId).contains(newHero));
//        assertTrue(squadDao.getAllHeroesBySquad(squadId).contains(newHero1));
//        assertFalse(squadDao.getAllHeroesBySquad(squadId).contains(newHero2));      //validation
//
//    }


    //helper method
    public Squad setupNewSquad(){return new Squad("justice league", "Removing all evil", 5);}
}