import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.Sql2oSquadDao;
import dao.Sql2oHeroDao;
import models.Squad;
import models.Hero;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import static spark.Spark.*;

public class App {

    static int getHerokuAssignedPort(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null){
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;    //return default port if heroku-port isn't set (i.e. on localhost)
    }

    public static void main(String[] args) { //type “psvm + tab” to autocreate this
        port(getHerokuAssignedPort());
        staticFileLocation("/public");
        String connectionString = "jdbc:postgresql://localhost:5432/herosquad";      //connect to todolist, not todolist_test!
        Sql2o sql2o = new Sql2o(connectionString, "postgres", "password");
//        String connectionString = "jdbc:postgresql://ec2-3-231-16-122.compute-1.amazonaws.com:5432/d36j2uacjqisnv";
//        Sql2o sql2o = new Sql2o(connectionString, "xafytqhmbmljhh", "2cf4f128e04650c32b9c9e15b5cb81f7187ee3e191e26c3898b00fd90adf25a4");
        Sql2oHeroDao heroDao = new Sql2oHeroDao(sql2o);
        Sql2oSquadDao squadDao = new Sql2oSquadDao(sql2o);


        //get: show all heroes in all squads and show all squads
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Squad> allSquads = squadDao.getAll();
            model.put("squads", allSquads);
            List<Hero> heroes = heroDao.getAll();
            model.put("heroes", heroes);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show a form to create a new squad
        get("/squads/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Squad> squads = squadDao.getAll(); //refresh list of links for navbar
            model.put("squads", squads);
            return new ModelAndView(model, "squad-form.hbs"); //new layout
        }, new HandlebarsTemplateEngine());

        //post: process a form to create a new squad
        post("/squads", (req, res) -> { //new
            //Map<String, Object> model = new HashMap<>();
            String squadName = req.queryParams("squadName");
            String causeDedicatedToFighting = req.queryParams("causeDedicatedToFighting");
            int squadMembersCounter = Integer.parseInt(req.queryParams("squadMembersCounter"));
            Squad newSquad = new Squad(squadName, causeDedicatedToFighting, squadMembersCounter);
            squadDao.add(newSquad);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());


        //get: delete all squads and all heroes
        get("/squads/delete", (req, res) -> {
            //Map<String, Object> model = new HashMap<>();
            squadDao.clearAllSquads();
            heroDao.clearAllHeroes();
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete all heroes
        get("/heroes/delete", (req, res) -> {
            //Map<String, Object> model = new HashMap<>();
            heroDao.clearAllHeroes();
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get a specific squad (and the hero it contains)
        get("/squads/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfSquadToFind = Integer.parseInt(req.params("id")); //new
            Squad foundSquad = squadDao.findById(idOfSquadToFind);
            model.put("squad", foundSquad);
            List<Hero> allHeroesBySquad = squadDao.getAllHeroesBySquad(idOfSquadToFind);
            model.put("heroes", allHeroesBySquad);
            model.put("squads", squadDao.getAll()); //refresh list of links for navbar
            int current = heroDao.getNumberOfHeroesInTheSameSquad(squadDao.findById(idOfSquadToFind).getId());
            int counter = squadDao.getAllHeroesBySquad(idOfSquadToFind).size();
            model.put("current", current);
//            if (counter == current){
//                model.put("full", "This Squad is Full!");
//                return new ModelAndView(model, "squad-detail.hbs"); //new
//            } else if (counter < current){
//                model.put("notFull", "This squad still has room.");
//                return new ModelAndView(model, "squad-detail.hbs"); //new
//            } else {
//                return null;
//            }
            return new ModelAndView(model, "squad-detail.hbs"); //new

        }, new HandlebarsTemplateEngine());

        //get: show a form to update a squad
        get("/squads/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("editSquad", true);
            Squad squad = squadDao.findById(Integer.parseInt(req.params("id")));
            model.put("squad", squad);
            model.put("squads", squadDao.getAll()); //refresh list of links for navbar
            return new ModelAndView(model, "squad-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process a form to update a squad
        post("/categories/:id", (req, res) -> {
            //Map<String, Object> model = new HashMap<>();
            int idOfSquadToEdit = Integer.parseInt(req.params("id"));
            String newSquadName = req.queryParams("newSquadName");
            String newCauseDedicatedToFighting = req.queryParams("newCauseDedicatedToFighting");
            int newSquadMembersCounter = Integer.parseInt(req.queryParams("newSquadMembersCounter"));
            squadDao.update(idOfSquadToEdit, newSquadName, newCauseDedicatedToFighting, newSquadMembersCounter);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete an individual hero
        get("/squads/:squad_id/heroes/:hero_id/delete", (req, res) -> {
            //Map<String, Object> model = new HashMap<>();
            int idOfHeroToDelete = Integer.parseInt(req.params("hero_id"));
            heroDao.deleteById(idOfHeroToDelete);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show new hero form
        get("/heroes/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Squad> squads = squadDao.getAll();
            model.put("squads", squads);
            return new ModelAndView(model, "hero-form.hbs");
        }, new HandlebarsTemplateEngine());

        //task: process new hero form
        post("/heroes", (req, res) -> { //URL to make new hero on POST route
            Map<String, Object> model = new HashMap<>();
            List<Squad> allSquads = squadDao.getAll();
            model.put("squads", allSquads);
            String name = req.queryParams("name");
            String specialPower = req.queryParams("specialPower");
            int age = Integer.parseInt(req.queryParams("age"));
            String weakness = req.queryParams("weakness");
            int squadId = Integer.parseInt(req.queryParams("squadId"));
            Squad foundSquad = squadDao.findById(squadId);
            model.put("squad", foundSquad);
            Hero newHero = new Hero(name, weakness, specialPower, age, squadId);        //See what we did with the hard coded categoryId?
            int counterVerifier = squadDao.findById(squadId).getSquadMembersCounter();
            model.put("preset", counterVerifier);
            int actual = heroDao.getNumberOfHeroesInTheSameSquad(newHero.getId());
            model.put("current", actual);
            heroDao.add(newHero);
            res.redirect("/");
            return null;

//            List<Hero> heroesSoFar = heroDao.getAll();
//            for (Hero heroItem: heroesSoFar
//                 ) {
//                System.out.println(heroItem);
//            }
//            System.out.println(heroesSoFar);
        }, new HandlebarsTemplateEngine());

        //get: show an individual hero that is nested in a squad
        get("/squads/:squad_id/heroes/:hero_id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfHeroToFind = Integer.parseInt(req.params("hero_id")); //pull id - must match route segment
            Hero foundHero = heroDao.findById(idOfHeroToFind); //use it to find hero
            int idOfSquadToFind = Integer.parseInt(req.params("squad_id"));
            Squad foundSquad = squadDao.findById(idOfSquadToFind);
            model.put("squad", foundSquad);
            model.put("hero", foundHero); //add it to model for template to display
            model.put("squads", squadDao.getAll()); //refresh list of links for navbar
            return new ModelAndView(model, "hero-detail.hbs"); //individual hero page.
        }, new HandlebarsTemplateEngine());

        //get: show a form to update a hero
        get("/heroes/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Squad> allSquads = squadDao.getAll();
            model.put("squads", allSquads);
            Hero hero = heroDao.findById(Integer.parseInt(req.params("id")));
            model.put("hero", hero);
            model.put("editHero", true);
            return new ModelAndView(model, "hero-form.hbs");
        }, new HandlebarsTemplateEngine());

        //task: process a form to update a hero
        post("/heroes/:id", (req, res) -> { //URL to update hero on POST route
            Map<String, Object> model = new HashMap<>();
            int heroToEditId = Integer.parseInt(req.params("id"));
            String newName = req.queryParams("name");
            String specialPower = req.queryParams("specialPower");
            String weakness = req.queryParams("weakness");
            int age = Integer.parseInt(req.queryParams("age"));
            int squadId = Integer.parseInt(req.queryParams("squadId"));
            heroDao.update(heroToEditId, newName, specialPower, weakness, age, squadId);  // remember the hardcoded categoryId we placed? See what we've done to/with it?
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

    }
}
