package net.botwithus;

import net.botwithus.api.game.hud.Dialog;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.api.game.hud.inventories.Inventory;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.events.impl.SkillUpdateEvent;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.util.Regex;


import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class SkeletonScript extends LoopingScript {
    private BotState botState = BotState.IDLE;
    private Random random = new Random();

    /////////////////////////////////////Botstate//////////////////////////
    enum BotState {
        //define your own states here
        IDLE,
        SLICING,
        BANKING,
        NEWS,
        NEWSBANK,
        STEWS,
        STEWSBANK,
        ROTTING,
        BANKROT,
        //...
    }

    //Make a hashmap for different slices and their different ComponentID1 2 3
    private final Map<String, List<Integer>> sliceMap = new HashMap<>();
    private String selectedSlicable = "Pineapple ring"; // Default selection
    public String[] getSlicableNames() {
        return sliceMap.keySet().toArray(new String[0]);
    }

    // Method to set the selected slicable
    public void setSelectedSlicable(String slicable) {
        if (sliceMap.containsKey(slicable)) {
            this.selectedSlicable = slicable;
        }
    }

    public SkeletonScript(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        sliceMap.put("Pineapple ring", List.of(1, 49, 89849878));
        sliceMap.put("Sliced banana", List.of(1, 65, 89849878));
        sliceMap.put("Watermelon slice", List.of(1, 61, 89849878));
        sliceMap.put("Orange slice", List.of(1, 41, 89849878));
        sliceMap.put("Orange chunks", List.of(1, 37, 89849878));
        this.sgc = new SkeletonScriptGraphicsContext(getConsole(), this);
    }

    @Override
    public void onLoop() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN) {
            Execution.delay(random.nextLong(3000, 7000));
            return;
        }

        switch (botState) {
            case IDLE -> {
                println("We're idle!");
                Execution.delay(random.nextLong(1000, 3000));
            }
            case SLICING -> {
                Execution.delay(handleSlicing(player));
            }
            case BANKING -> {
                Execution.delay(Banking());
            }
            case STEWS -> {
                Execution.delay(handleStews(player));
            }
            case STEWSBANK -> {
                Execution.delay(StewBanking());
            }
            case NEWS -> {
                Execution.delay(handleNews(player));
            }
            case NEWSBANK -> {
                Execution.delay(NewsBanking());
            }
            case ROTTING -> {
                Execution.delay(HandleRotting());
            }
            case BANKROT -> {
                Execution.delay(RotBank());
            }
        }
    }

    private long handleSlicing(LocalPlayer player) {
        Item Slicable = InventoryItemQuery.newQuery(93).option("Slice").results().first();
        if (Slicable == null) {
            println("We don't have any Slicables energy in our inventory.");
            botState = BotState.BANKING;
            return random.nextLong(500, 1000);
        } else {
            List<Integer> componentIDs = sliceMap.get(selectedSlicable);
            Execution.delay(random.nextLong(500, 1000));
            println("Interacting with fruit: " + Backpack.interact(Slicable.getName(), "Slice"));
            Execution.delay(random.nextLong(500, 1000));
            println("Selecting end product: " + MiniMenu.interact(ComponentAction.COMPONENT.getType(), componentIDs.get(0), componentIDs.get(1), componentIDs.get(2))) ;
            Execution.delay(random.nextLong(2000, 3000));
            println("Start cutting: " + MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350));
            Execution.delay(random.nextLong(2000, 3000));
            delayUntil(35000, () -> !Interfaces.isOpen(1251));
            return random.nextLong(500, 1000);
        }
    }

    private long Banking() {
        Npc banks = NpcQuery.newQuery().option("Load Last Preset from").results().nearest();
        SceneObject bankBooth = SceneObjectQuery.newQuery().option("Load Last Preset from").results().nearest();
        if (banks != null) {
            println("Yay, we found our bank.");
            println("Interacted bank: " + banks.interact("Load Last Preset from"));
            Execution.delay(random.nextLong(500, 1000));
            Item Slicable = InventoryItemQuery.newQuery(93).option("Slice").results().first();
            if (Slicable == null) {
                println("We don't have any Slicables in our inventory.");
                Execution.delay(random.nextLong(500, 2000));
                botState = BotState.IDLE;
                return random.nextLong(500, 1000);
            }
            Execution.delay(random.nextLong(500, 2000));
            botState = BotState.SLICING;
        }
        else if (bankBooth != null) {
            println("Yay, we found our bank booth.");
            Execution.delay(random.nextLong(500, 1000));
            println("Interacted bank booth: " + bankBooth.interact("Load Last Preset from"));
            Execution.delay(random.nextLong(500, 1000));
            Item Slicable = InventoryItemQuery.newQuery(93).option("Slice").results().first();
            if (Slicable == null) {
                println("We don't have any Slicables in our inventory.");
                Execution.delay(random.nextLong(500, 2000));
                botState = BotState.IDLE;
                return random.nextLong(500, 1000);
            }
            Execution.delay(random.nextLong(500, 2000));
            botState = BotState.SLICING;
        }
        else {
            println("Bank was null.");
        }
        return random.nextLong(1500,3000);
    }

    private long handleNews(LocalPlayer player) {
        Area.Rectangular news = new Area.Rectangular(new Coordinate(3216,3429,0), new Coordinate(3222,3435,0));
        if (Movement.traverse(NavPath.resolve(news).interrupt(event -> botState == BotState.IDLE)) == TraverseEvent.State.FINISHED) {
            println("Traversed to news");
            if (Backpack.isFull()) {
                println("Backpack full");
                botState = BotState.NEWSBANK;
                return random.nextLong(500, 1000);
            } else {
                Npc Benny = NpcQuery.newQuery().name("Benny").results().nearest();
                Execution.delay(random.nextLong(500, 1000));
                Benny.interact("Talk to");
                Execution.delay(random.nextLong(500, 1000));
                if (Dialog.isOpen());
                {
                    Execution.delay(random.nextLong(500, 1000));
                    Dialog.interact("Can I have a newspaper, please?");
                    Execution.delay(random.nextLong(500, 1000));
                    Dialog.select();
                    Execution.delay(random.nextLong(500, 1000));
                    Dialog.select();
                    Execution.delay(random.nextLong(500, 1000));
                    Dialog.interact("Sure, here you go.");
                    return random.nextLong(500, 1000);
                }
            }
        }
        return random.nextLong(500, 1000);
    }

    private long NewsBanking(){
        Area.Rectangular bank = new Area.Rectangular(new Coordinate(3189,3435,0), new Coordinate(3187,3443,0));
        SceneObject banks = SceneObjectQuery.newQuery().name("Bank booth").results().nearest();
        if (Movement.traverse(NavPath.resolve(bank).interrupt(event -> botState == BotState.IDLE)) == TraverseEvent.State.FINISHED) {
            println("Traversed to bank");
            if (banks != null) {
                Execution.delay(random.nextLong(500, 1000));
                banks.interact("Bank");
                Execution.delay(random.nextLong(500, 1000));
                Bank.depositAll();
            }
            if (Backpack.isEmpty());{
                botState = BotState.NEWS;
            }
        }
        return random.nextLong(500, 1000);
    }

    private long handleStews(LocalPlayer player) {
        Area.Rectangular stews = new Area.Rectangular(new Coordinate(2693,3498,0), new Coordinate(2689,3488,0));
        if (Movement.traverse(NavPath.resolve(stews).interrupt(event -> botState == BotState.IDLE)) == TraverseEvent.State.FINISHED) {
            println("Traversed to stews");
            if (Backpack.isFull()) {
                println("Backpack full");
                botState = BotState.STEWSBANK;
                return random.nextLong(500, 1000);
            } else {
                Npc Bartender = NpcQuery.newQuery().name("Bartender").results().nearest();
                Execution.delay(random.nextLong(500, 1000));
                Bartender.interact("Talk-to");
                Execution.delay(random.nextLong(500, 1000));
                if (Dialog.isOpen()) ;
                {
                    Execution.delay(random.nextLong(500, 1000));
                    Dialog.select();
                    Execution.delay(random.nextLong(500, 1000));
                    Dialog.interact("What do you have?");
                    Execution.delay(random.nextLong(500, 1000));
                    Dialog.select();
                    Execution.delay(random.nextLong(500, 1000));
                    Dialog.select();
                    Execution.delay(random.nextLong(500, 1000));
                    Dialog.interact("Could I have some stew please?");
                    Execution.delay(random.nextLong(500, 1000));
                    Dialog.select();
                    Execution.delay(random.nextLong(500, 1000));
                    Dialog.select();
                    Execution.delay(random.nextLong(500, 1000));
                    Dialog.select();
                    return random.nextLong(500, 1000);
                }
            }
        }
        return random.nextLong(500, 1000);
    }

    private long StewBanking(){
        Area.Rectangular bank = new Area.Rectangular(new Coordinate(2722,3491,0), new Coordinate(2730,3493,0));
        SceneObject banks = SceneObjectQuery.newQuery().name("Bank booth").results().nearest();
        if (Movement.traverse(NavPath.resolve(bank).interrupt(event -> botState == BotState.IDLE)) == TraverseEvent.State.FINISHED) {
            println("Traversed to bank");
            if (banks != null) {
                Execution.delay(random.nextLong(500, 1000));
                banks.interact("Bank");
                Execution.delay(random.nextLong(500, 1000));
                Bank.depositAll();
            }
            if (Backpack.isEmpty());{
                botState = BotState.STEWS;
            }
        }
        return random.nextLong(500, 1000);
    }

    private long HandleRotting(){
        Pattern cheesewheels = Regex.getPatternForContainsString("Cheese wheel");
        Item cheesewheel = InventoryItemQuery.newQuery(93).name(cheesewheels).results().first();
        Item cheese = InventoryItemQuery.newQuery(93).ids(1985).results().first();
        Area.Singular swamp = new Area.Singular(new Coordinate(3494,3455,0));
        if (cheesewheel == null && cheese == null) {
            Execution.delay(random.nextLong(500, 1000));
            botState = BotState.BANKROT;
        }
        else if (Movement.traverse(NavPath.resolve(swamp).interrupt(event -> botState == BotState.IDLE)) == TraverseEvent.State.FINISHED) {
            println("Traversed to swamp");
            if (cheese == null) {
                Execution.delay(random.nextLong(500, 1000));
                Backpack.interact(cheesewheel.getName(), "Slice");
                Execution.delay(random.nextLong(500, 1000));
                Backpack.interact(cheesewheel.getName(), "Slice");
                Execution.delay(random.nextLong(500, 1000));
                Backpack.interact(cheesewheel.getName(), "Slice");
                Execution.delay(random.nextLong(500, 1000));
                Backpack.interact(cheesewheel.getName(), "Slice");
            } else if (Backpack.isFull()){
                Execution.delay(random.nextLong(500, 1000));
                Item notePaper = InventoryItemQuery.newQuery().name("Magic notepaper").results().first();
                MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, notePaper.getSlot(), 96534533);
                println("Selected NotePaper");
                Execution.delay(random.nextLong(500, 1000));
                Item rottenfood = InventoryItemQuery.newQuery().name("Rotten food").results().first();
                MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, rottenfood.getSlot(), 96534533);
                println("Selected " + rottenfood);
            }
        }
        return random.nextLong(500, 1000);
    }

    private long RotBank(){
        Area.Singular bankarea = new Area.Singular(new Coordinate(3362,3397,0));
        SceneObject bank = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
        if (Movement.traverse(NavPath.resolve(bankarea).interrupt(event -> botState == BotState.IDLE)) == TraverseEvent.State.FINISHED) {
            println("Traversed to bank");
            if (bank != null) {
                Execution.delay(random.nextLong(500, 1000));
                bank.interact("Load Last Preset from");
            }
        }
        return random.nextLong(500, 1000);
    }

    ////////////////////Botstate/////////////////////
    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }
}

