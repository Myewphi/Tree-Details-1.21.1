package myewphi.treedetails.block;

import myewphi.treedetails.TreeDetails;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BranchBlock extends Block {
    public static final BooleanProperty NORTH = TreeDetails.NORTH;
    public static final BooleanProperty EAST = TreeDetails.EAST;
    public static final BooleanProperty SOUTH = TreeDetails.SOUTH;
    public static final BooleanProperty WEST = TreeDetails.WEST;
    public static final BooleanProperty UP = TreeDetails.UP;
    public static final BooleanProperty DOWN = TreeDetails.DOWN;

    public BranchBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE)
                .setValue(WEST, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE)
                .setValue(DOWN, Boolean.FALSE)
        );
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(NORTH, canConnect(context, Direction.NORTH))
                .setValue(EAST, canConnect(context, Direction.EAST))
                .setValue(SOUTH, canConnect(context, Direction.SOUTH))
                .setValue(WEST, canConnect(context, Direction.WEST))
                .setValue(UP, canConnect(context, Direction.UP))
                .setValue(DOWN, canConnect(context, Direction.DOWN));
    }
    boolean canConnect(BlockState state){
        return state.is(BlockTags.LOGS);
    }
    boolean canConnect(BlockPlaceContext context, Direction dir){
        return canConnect(context.getLevel().getBlockState(context.getClickedPos().relative(dir)));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state.setValue(TreeDetails.dirToBooleanProperty(direction), canConnect(neighborState));
    }

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
