package myewphi.treedetails.block;

import myewphi.treedetails.TreeDetails;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BranchBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty NORTH = TreeDetails.NORTH;
    public static final BooleanProperty EAST = TreeDetails.EAST;
    public static final BooleanProperty SOUTH = TreeDetails.SOUTH;
    public static final BooleanProperty WEST = TreeDetails.WEST;
    public static final BooleanProperty UP = TreeDetails.UP;
    public static final BooleanProperty DOWN = TreeDetails.DOWN;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;

    public BranchBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE)
                .setValue(WEST, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE)
                .setValue(DOWN, Boolean.FALSE)
                .setValue(WATERLOGGED, Boolean.FALSE)
                .setValue(PERSISTENT, Boolean.TRUE)
        );
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED, PERSISTENT);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED) ? 0 : 20;
    }

    // BLOCK STATE
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState()
                .setValue(NORTH, canConnect(context, Direction.NORTH))
                .setValue(EAST, canConnect(context, Direction.EAST))
                .setValue(SOUTH, canConnect(context, Direction.SOUTH))
                .setValue(WEST, canConnect(context, Direction.WEST))
                .setValue(UP, canConnect(context, Direction.UP))
                .setValue(DOWN, canConnect(context, Direction.DOWN))
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }
    boolean canConnect(BlockState state){
        return state.is(BlockTags.LOGS);
    }
    boolean canConnect(BlockPlaceContext context, Direction dir){
        return canConnect(context.getLevel().getBlockState(context.getClickedPos().relative(dir)));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if(!state.getValue(PERSISTENT) && neighborState.isAir()){
            if(level instanceof Level){
                level.scheduleTick(pos, this, 1);
            }
        }

        return state.setValue(TreeDetails.dirToBooleanProperty(direction), canConnect(neighborState));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(!isAttachedToTrunk(level, pos)){
            dropResources(state, level, pos);
            level.removeBlock(pos, false);
        }
    }

    protected boolean isAttachedToTrunk(Level level, BlockPos pos){
        List<BlockPos> checkedPos = new java.util.ArrayList<>(List.of());
        List<BlockPos> checkingPos = new java.util.ArrayList<>(List.of());
        checkingPos.add(pos);

        //use for loop to limit number of blocked check outwards
        for(int i = 0; i < 10; i++){
            List<BlockPos> willCheckPos = new java.util.ArrayList<>(List.of());

            //loop through all blocks currently added to the checking list
            for(BlockPos checking : checkingPos){
                //check adjacent directions
                for(Direction dir : Direction.values()){
                    //if not log tagged then skip
                    if(!level.getBlockState(checking.relative(dir)).is(BlockTags.LOGS)){
                        continue;
                    }
                    //if not a branch then this is our trunk!
                    if(!(level.getBlockState(checking.relative(dir)).getBlock() instanceof BranchBlock)){
                        return true;
                    }
                    //if branch then add it to the checking list
                    willCheckPos.add(checking.relative(dir));
                }

                //after looping through adjacent blocks, mark this spot as checked
                checkedPos.add(checking);
            }
            for(BlockPos willCheck : willCheckPos){
                checkingPos.add(willCheck);
            }
            //remove checked pos from checking list
            for(BlockPos checked : checkedPos){
                checkingPos.remove(checked);
            }
        }

        return false;
    }

    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    // VOXEL SHAPE
    private static final VoxelShape NORTH_BOX = Block.box(4, 4, 0, 12, 12, 4);
    private static final VoxelShape EAST_BOX = Block.box(12, 4, 4, 16, 12, 12);
    private static final VoxelShape SOUTH_BOX = Block.box(4, 4, 12, 12, 12, 16);
    private static final VoxelShape WEST_BOX = Block.box(0, 4, 4, 4, 12, 12);
    private static final VoxelShape UP_BOX = Block.box(4, 12, 4, 12, 16, 12);
    private static final VoxelShape DOWN_BOX = Block.box(4, 0, 4, 12, 4, 12);

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape base = Block.box(4, 4, 4, 12, 12, 12);
        if(state.getValue(NORTH).equals(Boolean.TRUE)){
            base = Shapes.or(base, NORTH_BOX);
        }
        if(state.getValue(EAST).equals(Boolean.TRUE)){
            base = Shapes.or(base, EAST_BOX);
        }
        if(state.getValue(SOUTH).equals(Boolean.TRUE)){
            base = Shapes.or(base, SOUTH_BOX);
        }
        if(state.getValue(WEST).equals(Boolean.TRUE)){
            base = Shapes.or(base, WEST_BOX);
        }
        if(state.getValue(UP).equals(Boolean.TRUE)){
            base = Shapes.or(base, UP_BOX);
        }
        if(state.getValue(DOWN).equals(Boolean.TRUE)){
            base = Shapes.or(base, DOWN_BOX);
        }
        return base;
    }
}
