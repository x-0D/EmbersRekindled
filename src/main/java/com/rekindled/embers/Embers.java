package com.rekindled.embers;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.rekindled.embers.api.item.ITyrfingWeapon;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.apiimpl.UpgradeUtilImpl;
import com.rekindled.embers.blockentity.render.AlchemyPedestalBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.AlchemyPedestalTopBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.AlchemyTabletBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.BinBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.CopperChargerBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.CrystalCellBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.EmberBoreBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.FluidTransferBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.FluidVesselBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.GeologicSeparatorBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.ItemTransferBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.MechanicalPumpBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.MelterTopBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.MixerCentrifugeBottomBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.MixerCentrifugeTopBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.ReservoirBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.StampBaseBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.StamperBlockEntityRenderer;
import com.rekindled.embers.datagen.EmbersBiomeModifiers;
import com.rekindled.embers.datagen.EmbersBlockStates;
import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.datagen.EmbersConfiguredFeatures;
import com.rekindled.embers.datagen.EmbersDamageTypeTags;
import com.rekindled.embers.datagen.EmbersDamageTypes;
import com.rekindled.embers.datagen.EmbersFluidTags;
import com.rekindled.embers.datagen.EmbersItemModels;
import com.rekindled.embers.datagen.EmbersItemTags;
import com.rekindled.embers.datagen.EmbersLang;
import com.rekindled.embers.datagen.EmbersLootModifiers;
import com.rekindled.embers.datagen.EmbersLootTables;
import com.rekindled.embers.datagen.EmbersPlacedFeatures;
import com.rekindled.embers.datagen.EmbersRecipes;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.entity.AncientGolemEntity;
import com.rekindled.embers.entity.render.AncientGolemRenderer;
import com.rekindled.embers.entity.render.EmberPacketRenderer;
import com.rekindled.embers.entity.render.EmberProjectileRenderer;
import com.rekindled.embers.gui.SlateScreen;
import com.rekindled.embers.item.EmberStorageItem;
import com.rekindled.embers.item.TyrfingItem;
import com.rekindled.embers.model.AncientGolemModel;
import com.rekindled.embers.network.PacketHandler;
import com.rekindled.embers.network.message.MessageWorldSeed;
import com.rekindled.embers.particle.AlchemyCircleParticle;
import com.rekindled.embers.particle.GlowParticle;
import com.rekindled.embers.particle.SmokeParticle;
import com.rekindled.embers.particle.SparkParticle;
import com.rekindled.embers.particle.StarParticle;
import com.rekindled.embers.particle.TyrfingParticle;
import com.rekindled.embers.particle.VaporParticle;
import com.rekindled.embers.research.ResearchManager;
import com.rekindled.embers.research.capability.IResearchCapability;
import com.rekindled.embers.util.DecimalFormats;
import com.rekindled.embers.util.Misc;

import net.minecraft.Util;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Embers.MODID)
public class Embers {

	public static final String MODID = "embersrekindled";

	public Embers() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::gatherData);
		modEventBus.addListener(this::registerCaps);
		modEventBus.addListener(this::entityAttributes);
		modEventBus.addListener(this::spawnPlacements);

		RegistryManager.BLOCKS.register(modEventBus);
		RegistryManager.ITEMS.register(modEventBus);
		RegistryManager.FLUIDTYPES.register(modEventBus);
		RegistryManager.FLUIDS.register(modEventBus);
		RegistryManager.ENTITY_TYPES.register(modEventBus);
		RegistryManager.BLOCK_ENTITY_TYPES.register(modEventBus);
		RegistryManager.CREATIVE_TABS.register(modEventBus);
		RegistryManager.PARTICLE_TYPES.register(modEventBus);
		RegistryManager.SOUND_EVENTS.register(modEventBus);
		RegistryManager.RECIPE_TYPES.register(modEventBus);
		RegistryManager.RECIPE_SERIALIZERS.register(modEventBus);
		RegistryManager.LOOT_MODIFIERS.register(modEventBus);
		RegistryManager.MENU_TYPES.register(modEventBus);
		EmbersSounds.init();
		//TODO: move this to apiimpl when I port that
		UpgradeUtil.IMPL = new UpgradeUtilImpl();

		ConfigManager.register();
	}

	public void commonSetup(final FMLCommonSetupEvent event) {
		PacketHandler.init();
		RegistryManager.registerDispenserBehaviour(event);
		MinecraftForge.EVENT_BUS.addListener(ResearchManager::onJoin);
		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, ResearchManager::attachCapability);
		MinecraftForge.EVENT_BUS.addListener(ResearchManager::onClone);
		ResearchManager.initResearches();
		MinecraftForge.EVENT_BUS.addListener(Embers::onJoin);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, Embers::onEntityDamaged);
		MinecraftForge.EVENT_BUS.addListener(Embers::onAnvilUpdate);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TagsUpdatedEvent.class, e -> Misc.tagItems.clear());
	}

	public void registerCaps(RegisterCapabilitiesEvent event) {
		event.register(IEmberCapability.class);
		event.register(IResearchCapability.class);
	}

	public void entityAttributes(EntityAttributeCreationEvent event) {
		event.put(RegistryManager.ANCIENT_GOLEM.get(), AncientGolemEntity.createAttributes().build());
	}

	public void spawnPlacements(SpawnPlacementRegisterEvent event) {
		event.register(RegistryManager.ANCIENT_GOLEM.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
	}

	public void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		if (event.includeClient()) {
			gen.addProvider(true, new EmbersLang(output));
			ItemModelProvider itemModels = new EmbersItemModels(output, existingFileHelper);
			gen.addProvider(true, itemModels);
			gen.addProvider(true, new EmbersBlockStates(output, existingFileHelper));
			gen.addProvider(true, new EmbersSounds(output, existingFileHelper));
		} if (event.includeServer()) {
			gen.addProvider(true, new EmbersLootTables(output));
			gen.addProvider(true, new EmbersRecipes(output));
			BlockTagsProvider blockTags = new EmbersBlockTags(output, lookupProvider, existingFileHelper);
			gen.addProvider(true, blockTags);
			gen.addProvider(true, new EmbersItemTags(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
			gen.addProvider(true, new EmbersFluidTags(output, lookupProvider, existingFileHelper));
			gen.addProvider(true, new DatapackBuiltinEntriesProvider(output, lookupProvider, new RegistrySetBuilder()
					.add(Registries.CONFIGURED_FEATURE, bootstrap -> EmbersConfiguredFeatures.generate(bootstrap)) //it doesn't like this one for some reason
					.add(Registries.PLACED_FEATURE, EmbersPlacedFeatures::generate)
					.add(ForgeRegistries.Keys.BIOME_MODIFIERS, EmbersBiomeModifiers::generate)
					.add(Registries.DAMAGE_TYPE, EmbersDamageTypes::generate),
					Set.of(MODID)));
			gen.addProvider(true, new EmbersDamageTypeTags(output, lookupProvider, existingFileHelper));
			gen.addProvider(true, new EmbersLootModifiers(output));
		}
	}

	public static void onJoin(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof ServerPlayer && !event.getLevel().isClientSide()) {
			ServerPlayer player = (ServerPlayer) event.getEntity();
			PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new MessageWorldSeed(((ServerLevel) event.getLevel()).getSeed()));
		}
	}

	public static void onEntityDamaged(LivingHurtEvent event) {
		final Entity source = event.getSource().getEntity();
		if (source instanceof LivingEntity livingSource) {
			final ItemStack heldStack = livingSource.getMainHandItem();
			if (heldStack.isEmpty())
				return;
			if (heldStack.getItem() instanceof ITyrfingWeapon tyrfing) {
				tyrfing.attack(event, event.getEntity().getAttribute(Attributes.ARMOR).getValue());
			}
		}
	}

	public static void onAnvilUpdate(AnvilUpdateEvent event) {
		if (event.getLeft().isRepairable() && !event.getLeft().is(EmbersItemTags.MATERIA_BLACKLIST) && event.getRight().getItem() == RegistryManager.ISOLATED_MATERIA.get()) {
			int i = 0;
			int j = 0;
			int k = 0;
			int l2 = Math.min(event.getLeft().getDamageValue(), event.getLeft().getMaxDamage() / 4);
			if (l2 <= 0) {
				event.setOutput(ItemStack.EMPTY);
				event.setCost(0);
				return;
			}
			int i3;
			ItemStack copy = event.getLeft().copy();
			for (i3 = 0; l2 > 0 && i3 < event.getRight().getCount(); ++i3) {
				int j3 = copy.getDamageValue() - l2;
				copy.setDamageValue(j3);
				i++;
				l2 = Math.min(event.getLeft().getDamageValue(), event.getLeft().getMaxDamage() / 4);
			}
			event.setMaterialCost(i3);

			if (event.getName() != null && !Util.isBlank(event.getName())) {
				if (!event.getName().equals(event.getLeft().getHoverName().getString())) {
					k = 1;
					i += k;
					copy.setHoverName(Component.literal(event.getName()));
				}
			} else if (event.getLeft().hasCustomHoverName()) {
				k = 1;
				i += k;
				copy.resetHoverName();
			}
			event.setCost(j + i);
			if (i <= 0) {
				copy = ItemStack.EMPTY;
			}
			if (k == i && k > 0 && event.getCost() >= 40) {
				event.setCost(39);
			}
			if (event.getCost() >= 40 && !event.getPlayer().getAbilities().instabuild) {
				copy = ItemStack.EMPTY;
			}
			if (!copy.isEmpty()) {
				int k2 = copy.getBaseRepairCost();

				if (k != i || k == 0) {
					k2 = k2 * 2 + 1;
				}

				copy.setRepairCost(k2);
			}
			event.setOutput(copy);
		}
	}

	@Mod.EventBusSubscriber(modid = Embers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ClientModEvents {

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
			modEventBus.addListener(EmbersClientEvents::onModelRegister);
			modEventBus.addListener(EmbersClientEvents::onModelBake);
			modEventBus.addListener(EmbersClientEvents::afterModelBake);
			MinecraftForge.EVENT_BUS.addListener(EmbersClientEvents::onClientTick);
			MinecraftForge.EVENT_BUS.addListener(EmbersClientEvents::onBlockHighlight);
			MinecraftForge.EVENT_BUS.addListener(EmbersClientEvents::onLevelRender);
			event.enqueueWork(() -> MenuScreens.register(RegistryManager.SLATE_MENU.get(), SlateScreen::new));
			ItemBlockRenderTypes.setRenderLayer(RegistryManager.STEAM.FLUID.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(RegistryManager.STEAM.FLUID_FLOW.get(), RenderType.translucent());
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void overlayRegister(RegisterGuiOverlaysEvent event) {
			event.registerAboveAll("embers_ingame_overlay", EmbersClientEvents.INGAME_OVERLAY);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void addResourceListener(RegisterClientReloadListenersEvent event) {
			event.registerReloadListener(new DecimalFormats());
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void addParticleProvider(RegisterParticleProvidersEvent event) {
			event.registerSprite(RegistryManager.GLOW_PARTICLE.get(), new GlowParticle.Provider());
			event.registerSprite(RegistryManager.STAR_PARTICLE.get(), new StarParticle.Provider());
			event.registerSprite(RegistryManager.SPARK_PARTICLE.get(), new SparkParticle.Provider());
			event.registerSprite(RegistryManager.SMOKE_PARTICLE.get(), new SmokeParticle.Provider());
			event.registerSprite(RegistryManager.VAPOR_PARTICLE.get(), new VaporParticle.Provider());
			event.registerSprite(RegistryManager.ALCHEMY_CIRCLE_PARTICLE.get(), new AlchemyCircleParticle.Provider());
			event.registerSprite(RegistryManager.TYRFING_PARTICLE.get(), new TyrfingParticle.Provider());
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
			event.registerEntityRenderer(RegistryManager.EMBER_PACKET.get(), EmberPacketRenderer::new);
			event.registerEntityRenderer(RegistryManager.EMBER_PROJECTILE.get(), EmberProjectileRenderer::new);
			event.registerEntityRenderer(RegistryManager.ANCIENT_GOLEM.get(), AncientGolemRenderer::new);

			event.registerBlockEntityRenderer(RegistryManager.EMBER_BORE_ENTITY.get(), EmberBoreBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.MELTER_TOP_ENTITY.get(), MelterTopBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.FLUID_VESSEL_ENTITY.get(), FluidVesselBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.STAMPER_ENTITY.get(), StamperBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.STAMP_BASE_ENTITY.get(), StampBaseBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.BIN_ENTITY.get(), BinBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.MIXER_CENTRIFUGE_BOTTOM_ENTITY.get(), MixerCentrifugeBottomBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.MIXER_CENTRIFUGE_TOP_ENTITY.get(), MixerCentrifugeTopBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.RESERVOIR_ENTITY.get(), ReservoirBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.CRYSTAL_CELL_ENTITY.get(), CrystalCellBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.GEOLOGIC_SEPARATOR_ENTITY.get(), GeologicSeparatorBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.COPPER_CHARGER_ENTITY.get(), CopperChargerBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.ITEM_TRANSFER_ENTITY.get(), ItemTransferBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.FLUID_TRANSFER_ENTITY.get(), FluidTransferBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.ALCHEMY_PEDESTAL_TOP_ENTITY.get(), AlchemyPedestalTopBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.ALCHEMY_PEDESTAL_ENTITY.get(), AlchemyPedestalBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.ALCHEMY_TABLET_ENTITY.get(), AlchemyTabletBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.MECHANICAL_PUMP_BOTTOM_ENTITY.get(), MechanicalPumpBlockEntityRenderer::new);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
			event.registerLayerDefinition(AncientGolemRenderer.LAYER_LOCATION, AncientGolemModel::createLayer);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
			event.register(new EmberStorageItem.ColorHandler(), RegistryManager.EMBER_JAR.get(), RegistryManager.EMBER_CARTRIDGE.get());
			event.register(new TyrfingItem.ColorHandler(), RegistryManager.TYRFING.get());
		}
	}
}
