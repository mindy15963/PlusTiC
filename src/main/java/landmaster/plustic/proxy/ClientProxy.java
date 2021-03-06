package landmaster.plustic.proxy;

import java.util.*;

import javax.annotation.*;
import landmaster.plustic.api.*;
import landmaster.plustic.entity.*;
import landmaster.plustic.entity.render.*;
import landmaster.plustic.modules.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.*;
import net.minecraft.client.settings.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.client.registry.*;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

import org.lwjgl.input.*;

import com.google.common.collect.*;

import slimeknights.tconstruct.common.*;
import slimeknights.tconstruct.library.*;
import slimeknights.tconstruct.library.client.*;
import slimeknights.tconstruct.library.materials.*;
import slimeknights.tconstruct.library.modifiers.*;
import slimeknights.tconstruct.library.tools.*;

public class ClientProxy extends CommonProxy {
	private static Map<String, KeyBinding> keyBindings;
	
	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
	    ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(ModInfo.MODID + ":" + id, "inventory"));
	}
	
	@Override
	public void setRenderInfo(Material mat, int color) {
		mat.setRenderInfo(color);
	}
	
	@Override
	public void setRenderInfo(Material mat, int lo, int mid, int hi) {
		mat.setRenderInfo(new MaterialRenderInfo.MultiColor(lo, mid, hi));
	}
	
	@Override
	public void registerFluidModels(Fluid fluid) {
		if (fluid == null) return;
		Block block = fluid.getBlock();
		if (block != null) {
			Item item = Item.getItemFromBlock(block);
			FluidStateMapper mapper = new FluidStateMapper(fluid);
			if (item != null) {
				ModelBakery.registerItemVariants(item);
				ModelLoader.setCustomMeshDefinition(item, mapper);
			}
			ModelLoader.setCustomStateMapper(block, mapper);
		}
	}
	
	@Override
	public void registerKeyBindings() {
		keyBindings = ImmutableMap.of(
				"release_entity", new KeyBinding("key.plustic_release_entity.desc", KeyConflictContext.IN_GAME, Keyboard.KEY_0, "key.categories.plustic"),
				"toggle_gui", new KeyBinding("key.plustic_toggle_gui.desc", KeyConflictContext.IN_GAME, Keyboard.KEY_I, "key.categories.plustic"),
				"set_portal", new KeyBinding("key.plustic_set_portal.desc", KeyConflictContext.IN_GAME, Keyboard.KEY_N, "key.categories.plustic"),
				"brown_magic", new KeyBinding("key.plustic_brown_magic.desc", KeyConflictContext.IN_GAME, Keyboard.KEY_O, "key.categories.plustic"),
				"toggle_tool", new KeyBinding("key.plustic_toggle_tool.desc", KeyConflictContext.IN_GAME, KeyModifier.ALT, Keyboard.KEY_COMMA, "key.categories.plustic"));
		for (KeyBinding kb: keyBindings.values()) ClientRegistry.registerKeyBinding(kb);
	}
	
	@Override
	public void registerToolModel(ToolCore tc) {
		ModelRegisterUtil.registerToolModel(tc);
	}
	
	@Override
	public void registerModifierModel(IModifier mod, ResourceLocation rl) {
		ModelRegisterUtil.registerModifierModel(mod, rl);
	}
	
	@Override
	public <T extends Item & IToolPart> void registerToolPartModel(T part) {
		ModelRegisterUtil.registerPartModel(part);
	}
	
	@Override
	public void initEntities() {
		super.initEntities();
		RenderingRegistry.registerEntityRenderingHandler(EntityBlindBandit.class, RenderBlindBandit::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySupremeLeader.class, RenderSupremeLeader::new);
	}
	
	@Override
	public void initToolGuis() {
		if (ModuleTools.katana != null) {
			ToolBuildGuiInfo katanaInfo = new ToolBuildGuiInfo(ModuleTools.katana);
			katanaInfo.addSlotPosition(33, 42 + 18); // handle
			katanaInfo.addSlotPosition(33 + 20, 42 - 20); // 1st blade
			katanaInfo.addSlotPosition(33, 42); // 2nd blade
			katanaInfo.addSlotPosition(33 - 18, 42 + 18); // binding
			TinkerRegistryClient.addToolBuilding(katanaInfo);
		}
		
		if (ModuleTools.laserGun != null) {
			ToolBuildGuiInfo laserGunInfo = new ToolBuildGuiInfo(ModuleTools.laserGun);
			laserGunInfo.addSlotPosition(7, 64);
			laserGunInfo.addSlotPosition(25, 38);
			laserGunInfo.addSlotPosition(49, 38);
			laserGunInfo.addSlotPosition(7, 38);
			TinkerRegistryClient.addToolBuilding(laserGunInfo);
		}
	}
	
	@Override
	public boolean isControlPressed(String control) {
		return keyBindings.get(control).isPressed();
	}
	
	public static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {
		public final Fluid fluid;
		public final ModelResourceLocation location;
		
		public FluidStateMapper(Fluid fluid) {
			this.fluid = fluid;
			this.location = new ModelResourceLocation(new ResourceLocation(ModInfo.MODID, "fluid_block"),
					fluid.getName());
		}
		
		@Nonnull
		@Override
		protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
			return location;
		}
		
		@Nonnull
		@Override
		public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
			return location;
		}
	}
}
