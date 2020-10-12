package sirttas.elementalcraft.particle.element;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sirttas.elementalcraft.ElementType;
import sirttas.elementalcraft.particle.AbstractECParticle;

@OnlyIn(Dist.CLIENT)
public class ParticleElementCrafting extends AbstractECParticle {

	private ParticleElementCrafting(World worldIn, Vec3d coord, IAnimatedSprite sprite, ElementType type) {
		super(worldIn, coord);
		this.motionX = (this.rand.nextFloat() - 0.5F);
		this.motionY = (this.rand.nextFloat() - 0.5F);
		this.motionZ = (this.rand.nextFloat() - 0.5F);
		this.prevPosX = coordX + motionX;
		this.prevPosY = coordY + motionY;
		this.prevPosZ = coordZ + motionZ;
		this.posX = this.prevPosX;
		this.posY = this.prevPosY;
		this.posZ = this.prevPosZ;
		this.particleScale = 0.1F * (this.rand.nextFloat() * 0.5F + 0.2F);
		float f = this.rand.nextFloat() * 0.4F + 0.6F;
		this.particleRed = f * type.getRed();
		this.particleGreen = f * type.getGreen();
		this.particleBlue = f * type.getBlue();
		this.canCollide = false;
		this.maxAge = this.rand.nextInt(10) + 5;
		this.selectSpriteRandomly(sprite);
	}

	@Override
	public void tick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.setExpired();
		} else {
			float f = (float) this.age / (float) this.maxAge;
			f = 1.0F - f;
			this.posX = this.coordX + this.motionX * f;
			this.posY = this.coordY + this.motionY * f;
			this.posZ = this.coordZ + this.motionZ * f;
		}
	}

	public static class Factory implements IParticleFactory<ElementTypeParticleData> {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite sprite) {
			this.spriteSet = sprite;
		}

		@Override
		public Particle makeParticle(ElementTypeParticleData data, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ParticleElementCrafting(worldIn, new Vec3d(x, y, z), this.spriteSet, data.getElementType());
		}
	}

}