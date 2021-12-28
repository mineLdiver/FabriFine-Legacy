package net.mine_diver.fabrifine.asm;

import com.chocohead.mm.api.ClassTinkerers;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;

public class ASMPlugin implements Runnable {

    @Override
    public void run() {
        ClassTinkerers.addTransformation("net.minecraft.class_66", classNode -> {
            for (FieldNode fieldNode : classNode.fields)
                switch (fieldNode.name) {
                    case "field_230":
                    case "field_249":
                        fieldNode.access |= Opcodes.ACC_VOLATILE;
                        break;
                }
        });
        ClassTinkerers.addTransformation("net.minecraft.client.render.Tessellator", classNode -> {
            for (FieldNode fieldNode : classNode.fields)
                if (fieldNode.name.equals("INSTANCE")) {
                    fieldNode.access |= Opcodes.ACC_VOLATILE;
                    fieldNode.access &= ~Opcodes.ACC_FINAL;
                    break;
                }
        });
    }
}
