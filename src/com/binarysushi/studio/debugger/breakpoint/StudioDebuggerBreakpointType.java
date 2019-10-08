package com.binarysushi.studio.debugger.breakpoint;

import com.intellij.lang.javascript.JavaScriptFileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StudioDebuggerBreakpointType extends XLineBreakpointType<XBreakpointProperties> {
    private static final String ID = "StudioDebuggerBreakpoint";
    private static final String TITLE = "Studio Debugger Breakpoint";


    protected StudioDebuggerBreakpointType() {
        super(ID, TITLE);
    }

    @Nullable
    @Override
    public XBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        return null;
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        return FileTypeRegistry.getInstance().isFileOfType(file, JavaScriptFileType.INSTANCE);
    }
}
