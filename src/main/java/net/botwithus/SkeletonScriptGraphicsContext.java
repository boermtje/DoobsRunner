package net.botwithus;

import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

import java.util.HashMap;
import java.util.Map;

public class SkeletonScriptGraphicsContext extends ScriptGraphicsContext {
    private SkeletonScript script;
    private NativeInteger selectedSlicableIndex = new NativeInteger(0);

    public SkeletonScriptGraphicsContext(ScriptConsole scriptConsole, SkeletonScript script) {
        super(scriptConsole);
        this.script = script;
        // Initialize arrays with current values
    }

    @Override
    public void drawSettings () {
        if (ImGui.Begin("DoobsRunner", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("My bar", ImGuiWindowFlag.None.getValue())) {
                    if (ImGui.BeginTabItem("Slicer", ImGuiWindowFlag.None.getValue())) {
                        ImGui.Text("My scripts state is: " + script.getBotState());
                        if (ImGui.Button("Start")) {
                            //button has been clicked
                            script.setBotState(SkeletonScript.BotState.SLICING);
                        }
                        ImGui.SameLine();
                        if (ImGui.Button("Stop")) {
                            //has been clicked
                            script.setBotState(SkeletonScript.BotState.IDLE);
                        }
                        // Slicable selection combo box
                        String[] slicables = script.getSlicableNames();
                        if (ImGui.Combo("What to slice", selectedSlicableIndex, slicables)) {
                            script.setSelectedSlicable(slicables[selectedSlicableIndex.get()]);
                        }
                        ImGui.EndTabItem();
                    }
                ImGui.EndTabBar();
            }
            ImGui.End();
        }
    }

    @Override
    public void drawOverlay() { super.drawOverlay(); }
}
