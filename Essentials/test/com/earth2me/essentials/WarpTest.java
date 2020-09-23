package com.earth2me.essentials;

import com.earth2me.essentials.api.IWarps;
import com.earth2me.essentials.commands.Commandsetwarp;
import com.earth2me.essentials.commands.WarpNotFoundException;
import net.ess3.api.IEssentials;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Location;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class WarpTest {

    private static final String EXISTING_WARP_NAME = "existingWarp".toLowerCase();
    private static final String NONEXISTENT_WARP_NAME = "nonExistentWarp".toLowerCase();

    private final Commandsetwarp command = new Commandsetwarp();

    @Mock
    private IEssentials essentialsMock;
    @Mock
    private User userMock;
    @Mock
    private IWarps warps;

    @Before
    public void init() throws InvalidWorldException, WarpNotFoundException {
        MockitoAnnotations.initMocks(this);

        command.setEssentials(essentialsMock);

        when(essentialsMock.getWarps()).thenReturn(warps);

        when(warps.getWarp(NONEXISTENT_WARP_NAME)).thenReturn(null);
        when(warps.getWarp(EXISTING_WARP_NAME)).thenReturn(Mockito.mock(Location.class));
    }

    @Test
    public void testCanCreateNonExistentWarp() throws Exception {
        grantCreate(NONEXISTENT_WARP_NAME, true);

        run(NONEXISTENT_WARP_NAME);
        verify(warps, times(1)).setWarp(any(User.class), anyString(), isNull());
    }

    @Test
    public void testCanCreateNonExistentWarpWithOverwrite() throws Exception {
        grantOverwrite(NONEXISTENT_WARP_NAME, true);

        run(NONEXISTENT_WARP_NAME);
        verify(warps, times(1)).setWarp(any(User.class), anyString(), isNull());
    }

    @Test(expected = Exception.class)
    public void testCanNotCreateNonExistentWarp() throws Exception {
        grantCreate(NONEXISTENT_WARP_NAME, false);
        grantOverwrite(NONEXISTENT_WARP_NAME, false);

        run(NONEXISTENT_WARP_NAME);
        verify(warps, times(0)).setWarp(any(User.class), anyString(), isNull());
    }

    @Test
    public void testCanOverwriteExistingWarp() throws Exception {
        grantCreate(EXISTING_WARP_NAME, false);
        grantOverwrite(EXISTING_WARP_NAME, true);

        run(EXISTING_WARP_NAME);
        verify(warps, times(1)).setWarp(any(User.class), anyString(), isNull());
    }

    @Test(expected = Exception.class)
    public void testCanNotOverwriteExistingWarp() throws Exception {
        grantCreate(EXISTING_WARP_NAME, false);
        grantOverwrite(EXISTING_WARP_NAME, false);

        run(EXISTING_WARP_NAME);
        verify(warps, times(0)).setWarp(any(User.class), anyString(), isNull());
    }

    private void run(String warpName) throws Exception {
        command.run(new FakeServer(), userMock, "setwarp", new String[]{warpName});
    }

    private void grantCreate(String warpName, boolean value) {
        when(userMock.isAuthorized("essentials.warp.set." + warpName)).thenReturn(value);
    }

    private void grantOverwrite(String warpName, boolean value) {
        when(userMock.isAuthorized("essentials.warp.overwrite." + warpName)).thenReturn(value);
    }

}
