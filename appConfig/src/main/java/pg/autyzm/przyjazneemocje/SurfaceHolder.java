package pg.autyzm.przyjazneemocje;

import android.graphics.Rect;
import android.view.Surface;

public interface SurfaceHolder {
    public abstract Surface getSurface ();
    public abstract Rect getSurfaceFrame ();
    public abstract boolean isCreating ();

}
