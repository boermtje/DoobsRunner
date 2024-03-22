package net.botwithus;

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
        SKILLING,
        BANKING,
        //...
    }

    public SkeletonScript(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
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
            case SKILLING -> {
                Execution.delay(handleSkilling(player));
            }
            case BANKING -> {
                Execution.delay(Banking());
            }
        }
    }

    private long handleSkilling(LocalPlayer player) {
        Item Gleaming = InventoryItemQuery.newQuery(93).name("Gleaming energy").results().first();
        if (Gleaming == null) {
            println("We don't have any Gleaming energy in our inventory.");
            botState = BotState.BANKING;
            return random.nextLong(500,1000);
        }
        else {
            println("We have Gleaming energy in our inventory.");
            Backpack.interact(Gleaming.getName(), "Weave");
            Execution.delay(random.nextLong(500, 1000));
            MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, 21, 89849878);
            Execution.delay(random.nextLong(500, 1000));
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
            return random.nextLong(10000,12000);
        }
    }

    private long Banking() {
        Npc banks = NpcQuery.newQuery().option("Load Last Preset from").results().nearest();
        SceneObject bankBooth = SceneObjectQuery.newQuery().option("Load Last Preset from").results().nearest();
        if (banks != null) {
            println("Yay, we found our bank.");

            println("Interacted bank: " + banks.interact("Load Last Preset from"));
            Execution.delay(random.nextLong(500, 2000));
            botState = BotState.SKILLING;
        }
        else if (bankBooth != null) {
            println("Yay, we found our bank booth.");
            Execution.delay(random.nextLong(500, 1000));
            println("Interacted bank booth: " + bankBooth.interact("Load Last Preset from"));
            Execution.delay(random.nextLong(500, 2000));
            botState = BotState.SKILLING;
        }
        else {
            println("Bank was null.");
        }
        return random.nextLong(1500,3000);
    }

    ////////////////////Botstate/////////////////////
    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }
}

