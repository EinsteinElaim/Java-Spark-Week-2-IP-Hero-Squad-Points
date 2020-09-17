package dao;

import models.Hero;
import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;

public class Sql2oHeroDaoTest {
    private static  Sql2oHeroDao heroDao;
    private static Connection conn;

    @BeforeClass
    public static void setUp() throws Exception {
        String connectionString = "jdbc:postgresql://localhost:5432/herosquad_test";    //connect to postgres test database
        Sql2o sql2o = new Sql2o(connectionString, "postgres", "password");
//        String connectionString = "jdbc:postgresql://ec2-54-197-48-79.compute-1.amazonaws.com:5432/dd9atnupft6c3p";
//        Sql2o sql2o = new Sql2o(connectionString, "wwrapikqmdhwkt", "9ef20a089add951f7b577926fce905a990551560008cd2e89f96a41ade30c47c");
        heroDao = new Sql2oHeroDao(sql2o);
        conn = sql2o.open();    // open connection once before this test file is run
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("clearing batabase");
        heroDao.clearAllHeroes();
    }

    @AfterClass
    public static void shutdown() throws Exception {
        conn.close();
        System.out.println("connection closed");
    }

    @Test
    public void addingHeroSetsId() throws Exception {
        Hero hero = setUpNewHero();
        int originalHeroId = hero.getId();
        heroDao.add(hero);
        assertNotEquals(originalHeroId, hero.getId());
    }

//    @Test
//    public void existingHeroesCanBeFoundById()throws Exception{
//        Hero hero = setUpNewHero();
//        heroDao.add(hero);  //add to dao to save it
//        Hero foundHero = heroDao.findById(hero.getId());    //retrieve
//        assertEquals(hero, foundHero);
//    }

    @Test
    public void addedHeroesAreReturnedFromGetAll()throws Exception{
        Hero hero = setUpNewHero();
        heroDao.add(hero);
        assertEquals(1, heroDao.getAll().size());
    }

    @Test
    public void noHeroReturnsEmptyList()throws Exception{
        assertEquals(0, heroDao.getAll().size());
    }

    @Test
    public void updateChangesHeroContent()throws Exception{
        String initialName = "super man";
        Hero hero = setUpNewHero();
        heroDao.add(hero);

        heroDao.update(hero.getId(), "Super MAN", "fear of rats", "laser eyes", 25, 2);
        Hero updatedHero = heroDao.findById(hero.getId());
        assertNotEquals(initialName, updatedHero.getName());
    }

    @Test
    public void deleteByIdDeletesCorrectHero()throws Exception{
        Hero hero = setUpNewHero();
        heroDao.add(hero);
        heroDao.deleteById(hero.getId());
        assertEquals(0, heroDao.getAll().size());
    }

    @Test
    public void clearAllClearsAllHeroes() throws Exception {
        Hero hero = setUpNewHero();
        Hero anotherHero = new Hero("bat man", "women", "money, guns", 24, 2);
        heroDao.add(hero);
        heroDao.add(anotherHero);
        int heroDaoSize = heroDao.getAll().size();
        heroDao.clearAllHeroes();
        assertTrue(heroDaoSize > 0 && heroDaoSize >heroDao.getAll().size());
    }

    @Test
    public void squadIdIsReturnedCorrectly()throws Exception{
        Hero hero = setUpNewHero();
        int originalSquadId = hero.getSquadId();
        heroDao.add(hero);
        assertEquals(originalSquadId, heroDao.findById(hero.getId()).getSquadId());
    }

    @Test
    public void numberOfRecordsWithSameSquadIdIsReturnedCorrectly() throws Exception {
        Hero hero = setUpNewHero();
        Hero hero2 = setUpNewHero();
        Hero hero3 = setUpNewHero();
        Hero hero4 = new Hero("Super Man", "Kryptonite", "Laser Eyes", 25, 2);
        heroDao.add(hero);
        heroDao.add(hero2);
        heroDao.add(hero3);
        heroDao.add(hero4);
        int valueToSearchSquadIdUsing = heroDao.findById(hero.getId()).getSquadId();
        assertEquals(4, heroDao.getAll().size());
        assertEquals(3, heroDao.getNumberOfHeroesInTheSameSquad(valueToSearchSquadIdUsing));
    }



    //helper method for tests
    public Hero setUpNewHero(){return new Hero("super man", "kryptonite", "x-ray vision", 30, 1);}
}