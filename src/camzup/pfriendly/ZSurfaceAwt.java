package camzup.pfriendly;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URL;

import javax.swing.JFrame;

import java.util.ArrayList;
import java.util.List;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PSurface;
import processing.core.PSurfaceNone;

import processing.awt.ShimAWT;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class ZSurfaceAwt extends PSurfaceNone {
   protected Canvas canvas;
   protected Insets currentInsets = new Insets(0, 0, 0, 0);
   protected int cursorType = PConstants.ARROW;
   protected boolean cursorVisible = true;
   protected GraphicsDevice displayDevice;
   protected Frame frame;
   protected List < Image > iconImages;
   protected Cursor invisibleCursor;
   protected Rectangle screenRect;
   protected int sketchHeight;
   protected int sketchWidth;
   protected int windowScaleFactor;

   public ZSurfaceAwt ( final PGraphics graphics ) {

      /* Copied from PSurfaceAWT on 03/16/2021. JB. */

      super(graphics);

      this.canvas = new SmoothCanvas();
      this.canvas.setFocusTraversalKeysEnabled(false);

      this.canvas.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized ( final ComponentEvent e ) {

            if ( !ZSurfaceAwt.this.sketch.isLooping() ) {
               final Dimension canvasSize = ZSurfaceAwt.this.canvas.getSize();
               if ( canvasSize.width != ZSurfaceAwt.this.sketch.sketchWidth()
                  || canvasSize.height != ZSurfaceAwt.this.sketch
                     .sketchHeight() ) {
                  ZSurfaceAwt.this.sketch.redraw();
               }
            }
         }

      });
      this.addListeners();
   }

   @Override
   public Thread createThread ( ) {

      return new AnimationThread() {
         @Override
         public void callDraw ( ) {

            ZSurfaceAwt.this.sketch.handleDraw();
            ZSurfaceAwt.this.render();
         }

      };
   }

   @Override
   public Object getNative ( ) { return this.canvas; }

   @Override
   public void hideCursor ( ) {

      if ( this.invisibleCursor == null ) {
         final BufferedImage cursorImg = new BufferedImage(16, 16,
            BufferedImage.TYPE_INT_ARGB);
         final Dimension cursorSize = Toolkit.getDefaultToolkit()
            .getBestCursorSize(16, 16);
         if ( cursorSize.width == 0 || cursorSize.height == 0 ) {
            this.invisibleCursor = Cursor.getDefaultCursor();
         } else {
            this.invisibleCursor = this.canvas.getToolkit().createCustomCursor(
               cursorImg, new Point(8, 8), "blank");
         }
      }
      this.canvas.setCursor(this.invisibleCursor);
      this.cursorVisible = false;
   }

   @Override
   public void initFrame ( final PApplet sk ) {

      this.sketch = sk;

      final GraphicsEnvironment environment = GraphicsEnvironment
         .getLocalGraphicsEnvironment();

      final int displayNum = this.sketch.sketchDisplay();
      if ( displayNum > 0 ) {
         final GraphicsDevice[] devices = environment.getScreenDevices();
         if ( displayNum <= devices.length ) {
            this.displayDevice = devices[displayNum - 1];
         } else {
            System.err.format("Display %d does not exist, "
               + "using the default display instead.%n", displayNum);
            for ( int i = 0; i < devices.length; i++ ) {
               System.err.format("Display %d is %s%n", i + 1, devices[i]);
            }
         }
      }
      if ( this.displayDevice == null ) {
         this.displayDevice = environment.getDefaultScreenDevice();
      }

      final boolean spanDisplays = this.sketch.sketchDisplay()
         == PConstants.SPAN;
      this.screenRect = spanDisplays ? ZSurfaceAwt.getDisplaySpan()
         : this.displayDevice.getDefaultConfiguration().getBounds();
      this.sketch.displayWidth = this.screenRect.width;
      this.sketch.displayHeight = this.screenRect.height;

      this.windowScaleFactor = PApplet.platform == PConstants.MACOS ? 1
         : this.sketch.pixelDensity;

      this.sketchWidth = this.sketch.sketchWidth() * this.windowScaleFactor;
      this.sketchHeight = this.sketch.sketchHeight() * this.windowScaleFactor;

      final boolean fullScreen = this.sketch.sketchFullScreen();

      if ( fullScreen || spanDisplays ) {
         this.sketchWidth = this.screenRect.width;
         this.sketchHeight = this.screenRect.height;
      }

      this.frame = new JFrame(this.displayDevice.getDefaultConfiguration());
      final Color windowColor = new Color(this.sketch.sketchWindowColor(),
         false);
      if ( this.frame instanceof JFrame ) {
         ( ( JFrame ) this.frame ).getContentPane().setBackground(windowColor);
      } else {
         this.frame.setBackground(windowColor);
      }

      this.setProcessingIcon(this.frame);

      this.frame.add(this.canvas);
      this.setSize(this.sketchWidth / this.windowScaleFactor, this.sketchHeight
         / this.windowScaleFactor);

      this.frame.setLayout(null);
      if ( fullScreen ) {
         this.frame.invalidate();
      } else {}

      this.frame.setResizable(false);

      this.frame.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing ( final WindowEvent e ) {
            ZSurfaceAwt.this.sketch.exit();
         }

      });

   }

   @Override
   public void initOffscreen ( final PApplet sk ) { this.sketch = sk; }

   @Override
   public void placePresent ( final int stopColor ) {

      this.setFullFrame();

      this.canvas.setBounds( ( this.screenRect.width - this.sketchWidth ) / 2,
         ( this.screenRect.height - this.sketchHeight ) / 2, this.sketchWidth,
         this.sketchHeight);

      if ( stopColor != 0 ) {
         final Label label = new Label("stop");
         label.setForeground(new Color(stopColor, false));
         label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed ( final java.awt.event.MouseEvent e ) {
               ZSurfaceAwt.this.sketch.exit();
            }

         });
         this.frame.add(label);

         Dimension labelSize = label.getPreferredSize();
         labelSize = new Dimension(100, labelSize.height);
         label.setSize(labelSize);
         label.setLocation(20, this.screenRect.height - labelSize.height - 20);
      }

   }

   @Override
   public void placeWindow ( final int[] location,
      final int[] editorLocation ) {

      final Dimension window = this.setFrameSize();

      final int contentW = Math.max(this.sketchWidth,
         PSurface.MIN_WINDOW_WIDTH);
      final int contentH = Math.max(this.sketchHeight,
         PSurface.MIN_WINDOW_HEIGHT);

      if ( this.sketch.sketchFullScreen() ) { this.setFullFrame(); }

      if ( !this.sketch.sketchFullScreen() ) {
         if ( location != null ) {
            this.frame.setLocation(location[0], location[1]);

         } else if ( editorLocation != null ) {
            int locationX = editorLocation[0] - 20;
            int locationY = editorLocation[1];

            if ( locationX - window.width > 10 ) {
               this.frame.setLocation(locationX - window.width, locationY);
            } else {
               locationX = ( this.sketch.displayWidth - window.width ) / 2;
               locationY = ( this.sketch.displayHeight - window.height ) / 2;
               this.frame.setLocation(locationX, locationY);
            }
         } else {
            this.setFrameCentered();
         }
         final Point frameLoc = this.frame.getLocation();
         if ( frameLoc.y < 0 ) { this.frame.setLocation(frameLoc.x, 30); }
      }

      this.canvas.setBounds( ( contentW - this.sketchWidth ) / 2, ( contentH
         - this.sketchHeight ) / 2, this.sketchWidth, this.sketchHeight);

      this.setupFrameResizeListener();
   }

   @Override
   public void selectFolder ( final String prompt, final String callback,
      final File file, final Object callbackObject ) {

      ShimAWT.selectFolder(prompt, callback, file, callbackObject);
   }

   @Override
   public void selectInput ( final String prompt, final String callback,
      final File file, final Object callbackObject ) {

      ShimAWT.selectInput(prompt, callback, file, callbackObject);
   }

   @Override
   public void selectOutput ( final String prompt, final String callback,
      final File file, final Object callbackObject ) {

      ShimAWT.selectOutput(prompt, callback, file, callbackObject);
   }

   @Override
   public void setAlwaysOnTop ( final boolean always ) {

      this.frame.setAlwaysOnTop(always);
   }

   @Override
   public void setCursor ( final int kind ) {

      int k = kind;
      if ( PApplet.platform == PConstants.MACOS && k == PConstants.MOVE ) {
         k = PConstants.HAND;
      }
      this.canvas.setCursor(Cursor.getPredefinedCursor(k));
      this.cursorVisible = true;
      this.cursorType = k;
   }

   @Override
   public void setCursor ( final PImage img, final int x, final int y ) {

      // TODO: TEST
      final Dimension cursorSize = Toolkit.getDefaultToolkit()
         .getBestCursorSize(img.width, img.height);
      if ( cursorSize.width == 0 || cursorSize.height == 0 ) { return; }
      final Cursor cursor = this.canvas.getToolkit().createCustomCursor(YupJ2
         .convertPImageToNative(img), new Point(x, y), "custom");
      this.canvas.setCursor(cursor);
      this.cursorVisible = true;
   }

   @Override
   public void setIcon ( final PImage image ) {

      final Image awtImage = YupJ2.convertPImageToNative(image);

      if ( PApplet.platform != PConstants.MACOS ) {
         this.frame.setIconImage(awtImage);

      } else {
         try {
            final String td = "processing.core.ThinkDifferent";
            final Class < ? > thinkDifferent = Thread.currentThread()
               .getContextClassLoader().loadClass(td);
            final Method method = thinkDifferent.getMethod("setIconImage",
               Image.class);
            method.invoke(null, awtImage);
         } catch ( final Exception e ) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void setLocation ( final int x, final int y ) {

      this.frame.setLocation(x, y);
   }

   @Override
   public void setResizable ( final boolean resizable ) {

      if ( this.frame != null ) { this.frame.setResizable(resizable); }
   }

   @Override
   public void setSize ( final int w, final int h ) {

      int wide = w;
      int high = h;

      if ( high <= 0 ) { high = 1; }
      if ( wide <= 0 ) { wide = 1; }

      if ( wide == this.sketch.width && high == this.sketch.height
         && ( this.frame == null || this.currentInsets.equals(this.frame
            .getInsets()) ) ) {
         return;
      }

      this.sketchWidth = wide * this.windowScaleFactor;
      this.sketchHeight = high * this.windowScaleFactor;

      if ( this.frame != null ) { this.setFrameSize(); }
      this.setCanvasSize();
      this.sketch.setSize(wide, high);
      this.graphics.setSize(wide, high);
   }

   /** Set the window (and dock, or whatever necessary) title. */
   @Override
   public void setTitle ( final String title ) {

      this.frame.setTitle(title);
      if ( this.cursorVisible && PApplet.platform == PConstants.MACOS
         && this.cursorType != PConstants.ARROW ) {
         this.hideCursor();
         this.showCursor();
      }
   }

   /**
    * Set this sketch to communicate its state back to the PDE.
    * <p/>
    * This uses the stderr stream to write positions of the window (so that it
    * will be saved by the PDE for the next run) and notify on quit. See more
    * notes in the Worker class.
    */
   @Override
   public void setupExternalMessages ( ) {

      this.frame.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentMoved ( final ComponentEvent e ) {

            final Point where = ( ( Frame ) e.getSource() ).getLocation();
            ZSurfaceAwt.this.sketch.frameMoved(where.x, where.y);
         }

      });
   }

   @Override
   public void setVisible ( final boolean visible ) {

      this.frame.setVisible(visible);

      if ( this.canvas != null ) { this.canvas.requestFocus(); }
   }

   @Override
   public void showCursor ( ) {

      if ( !this.cursorVisible ) {
         this.cursorVisible = true;
         this.canvas.setCursor(Cursor.getPredefinedCursor(this.cursorType));
      }
   }

   void debug ( final String format, final Object... args ) {

      System.out.format(format + "%n", args);
   }

   protected void addListeners ( ) {

      this.canvas.addMouseListener(new MouseListener() {

         @Override
         public void mouseClicked ( final java.awt.event.MouseEvent e ) {

            ZSurfaceAwt.this.nativeMouseEvent(e);
         }

         @Override
         public void mouseEntered ( final java.awt.event.MouseEvent e ) {

            ZSurfaceAwt.this.nativeMouseEvent(e);
         }

         @Override
         public void mouseExited ( final java.awt.event.MouseEvent e ) {

            ZSurfaceAwt.this.nativeMouseEvent(e);
         }

         @Override
         public void mousePressed ( final java.awt.event.MouseEvent e ) {

            ZSurfaceAwt.this.nativeMouseEvent(e);
         }

         @Override
         public void mouseReleased ( final java.awt.event.MouseEvent e ) {

            ZSurfaceAwt.this.nativeMouseEvent(e);
         }

      });

      this.canvas.addMouseMotionListener(new MouseMotionListener() {

         @Override
         public void mouseDragged ( final java.awt.event.MouseEvent e ) {

            ZSurfaceAwt.this.nativeMouseEvent(e);
         }

         @Override
         public void mouseMoved ( final java.awt.event.MouseEvent e ) {

            ZSurfaceAwt.this.nativeMouseEvent(e);
         }

      });

      this.canvas.addMouseWheelListener(new MouseWheelListener() {

         @Override
         public void mouseWheelMoved ( final MouseWheelEvent e ) {

            ZSurfaceAwt.this.nativeMouseEvent(e);
         }

      });

      this.canvas.addKeyListener(new KeyListener() {

         @Override
         public void keyPressed ( final java.awt.event.KeyEvent e ) {

            ZSurfaceAwt.this.nativeKeyEvent(e);
         }

         @Override
         public void keyReleased ( final java.awt.event.KeyEvent e ) {

            ZSurfaceAwt.this.nativeKeyEvent(e);
         }

         @Override
         public void keyTyped ( final java.awt.event.KeyEvent e ) {

            ZSurfaceAwt.this.nativeKeyEvent(e);
         }

      });

      this.canvas.addFocusListener(new FocusListener() {

         @Override
         public void focusGained ( final FocusEvent e ) {

            ZSurfaceAwt.this.sketch.focused = true;
            ZSurfaceAwt.this.sketch.focusGained();
         }

         @Override
         public void focusLost ( final FocusEvent e ) {

            ZSurfaceAwt.this.sketch.focused = false;
            ZSurfaceAwt.this.sketch.focusLost();
         }

      });
   }

   protected void nativeKeyEvent ( final java.awt.event.KeyEvent event ) {

      int peAction = 0;
      switch ( event.getID() ) {
         case java.awt.event.KeyEvent.KEY_PRESSED:
            peAction = KeyEvent.PRESS;
            break;

         case java.awt.event.KeyEvent.KEY_RELEASED:
            peAction = KeyEvent.RELEASE;
            break;

         case java.awt.event.KeyEvent.KEY_TYPED:
            peAction = KeyEvent.TYPE;
            break;

         default:
            break;
      }

      final int modifiers = event.getModifiersEx();

      this.sketch.postEvent(new KeyEvent(event, event.getWhen(), peAction,
         modifiers, event.getKeyChar(), event.getKeyCode()));
   }

   protected void nativeMouseEvent (
      final java.awt.event.MouseEvent nativeEvent ) {

      int peCount = nativeEvent.getClickCount();
      int peButton = 0;

      /*
       * This does not work for mouseClicked or mouseReleased. See
       * https://github.com/processing/processing4/issues/181 .
       */
      final int modifiers = nativeEvent.getModifiersEx();
      if ( ( modifiers & InputEvent.BUTTON1_DOWN_MASK ) != 0 ) {
         peButton = PConstants.LEFT;
      } else if ( ( modifiers & InputEvent.BUTTON2_DOWN_MASK ) != 0 ) {
         peButton = PConstants.CENTER;
      } else if ( ( modifiers & InputEvent.BUTTON3_DOWN_MASK ) != 0 ) {
         peButton = PConstants.RIGHT;
      }

      int peAction = 0;
      switch ( nativeEvent.getID() ) {
         case java.awt.event.MouseEvent.MOUSE_PRESSED:
            peAction = MouseEvent.PRESS;
            break;

         case java.awt.event.MouseEvent.MOUSE_RELEASED:
            peAction = MouseEvent.RELEASE;
            peButton = ZSurfaceAwt.awtToProcessingMouseButton(nativeEvent
               .getButton());
            break;

         case java.awt.event.MouseEvent.MOUSE_CLICKED:
            peAction = MouseEvent.CLICK;
            peButton = ZSurfaceAwt.awtToProcessingMouseButton(nativeEvent
               .getButton());
            break;

         case java.awt.event.MouseEvent.MOUSE_DRAGGED:
            peAction = MouseEvent.DRAG;
            break;

         case java.awt.event.MouseEvent.MOUSE_MOVED:
            peAction = MouseEvent.MOVE;
            break;

         case java.awt.event.MouseEvent.MOUSE_ENTERED:
            peAction = MouseEvent.ENTER;
            break;

         case java.awt.event.MouseEvent.MOUSE_EXITED:
            peAction = MouseEvent.EXIT;
            break;

         case java.awt.event.MouseEvent.MOUSE_WHEEL:
            peAction = MouseEvent.WHEEL;
            peCount = ( ( MouseWheelEvent ) nativeEvent ).getWheelRotation();
            break;

         default:
            break;
      }

      this.sketch.postEvent(new MouseEvent(nativeEvent, nativeEvent.getWhen(),
         peAction, modifiers, nativeEvent.getX() / this.windowScaleFactor,
         nativeEvent.getY() / this.windowScaleFactor, peButton, peCount));
   }

   synchronized protected void render ( ) {

      if ( this.canvas.isDisplayable() && this.graphics.image != null ) {
         if ( this.canvas.getBufferStrategy() == null ) {
            this.canvas.createBufferStrategy(2);
         }
         final BufferStrategy strategy = this.canvas.getBufferStrategy();
         if ( strategy != null ) {
            do {
               do {
                  final Graphics2D draw = ( Graphics2D ) strategy
                     .getDrawGraphics();
                  draw.drawImage(this.graphics.image, 0, 0, this.sketchWidth,
                     this.sketchHeight, null);
                  draw.dispose();
               } while ( strategy.contentsRestored() );

               strategy.show();

            } while ( strategy.contentsLost() );
         }
      }
   }

   protected void setProcessingIcon ( final Frame frame ) {

      if ( PApplet.platform != PConstants.MACOS ) {
         try {
            if ( this.iconImages == null ) {
               this.iconImages = new ArrayList <>();
               final int[] sizes = { 16, 32, 48, 64, 128, 256, 512 };

               for ( final int sz : sizes ) {
                  final URL url = PApplet.class.getResource("/icon/icon-" + sz
                     + ".png");
                  final Image image = Toolkit.getDefaultToolkit().getImage(url);
                  this.iconImages.add(image);
               }
            }
            frame.setIconImages(this.iconImages);

         } catch ( final Exception e ) {}

      } else {
         if ( !ZSurfaceAwt.dockIconSpecified() ) {
            final URL url = PApplet.class.getResource("/icon/icon-512.png");
            try {
               final String td = "processing.core.ThinkDifferent";
               final Class < ? > thinkDifferent = Thread.currentThread()
                  .getContextClassLoader().loadClass(td);
               final Method method = thinkDifferent.getMethod("setIconImage",
                  Image.class);
               method.invoke(null, Toolkit.getDefaultToolkit().getImage(url));
            } catch ( final Exception e ) {
               e.printStackTrace();
            }
         }
      }
   }

   private void setCanvasSize ( ) {

      final int contentW = Math.max(this.sketchWidth,
         PSurface.MIN_WINDOW_WIDTH);
      final int contentH = Math.max(this.sketchHeight,
         PSurface.MIN_WINDOW_HEIGHT);

      this.canvas.setBounds( ( contentW - this.sketchWidth ) / 2, ( contentH
         - this.sketchHeight ) / 2, this.sketchWidth, this.sketchHeight);
   }

   private void setFrameCentered ( ) {

      this.frame.setLocation(this.screenRect.x + ( this.screenRect.width
         - this.sketchWidth ) / 2, this.screenRect.y + ( this.screenRect.height
            - this.sketchHeight ) / 2);
   }

   /** Resize frame for these sketch (canvas) dimensions. */
   private Dimension setFrameSize ( ) {

      this.frame.addNotify();
      this.currentInsets = this.frame.getInsets();
      final int windowW = Math.max(this.sketchWidth, PSurface.MIN_WINDOW_WIDTH)
         + this.currentInsets.left + this.currentInsets.right;
      final int windowH = Math.max(this.sketchHeight,
         PSurface.MIN_WINDOW_HEIGHT) + this.currentInsets.top
         + this.currentInsets.bottom;
      this.frame.setSize(windowW, windowH);
      return new Dimension(windowW, windowH);
   }

   /** Hide the menu bar, make the Frame undecorated, set it to screenRect. */
   private void setFullFrame ( ) {

      PApplet.hideMenuBar();

      this.frame.removeNotify();
      this.frame.setUndecorated(true);
      this.frame.addNotify();

      this.frame.setBounds(this.screenRect);
   }

   /**
    * Set up a listener that will fire proper component resize events in cases
    * where frame.setResizable(true) is called.
    */
   private void setupFrameResizeListener ( ) {

      this.frame.addWindowStateListener(new WindowStateListener() {
         @Override
         public void windowStateChanged ( final WindowEvent e ) {

            if ( Frame.MAXIMIZED_BOTH == e.getNewState() ) {
               ZSurfaceAwt.this.frame.addNotify();
            }
         }

      });

      this.frame.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized ( final ComponentEvent e ) {

            if ( ZSurfaceAwt.this.frame.isResizable() ) {
               final Frame farm = ( Frame ) e.getComponent();
               if ( farm.isVisible() ) {
                  final Dimension windowSize = farm.getSize();
                  final int x = farm.getX()
                     + ZSurfaceAwt.this.currentInsets.left;
                  final int y = farm.getY()
                     + ZSurfaceAwt.this.currentInsets.top;
                  final int w = windowSize.width
                     - ZSurfaceAwt.this.currentInsets.left
                     - ZSurfaceAwt.this.currentInsets.right;
                  final int h = windowSize.height
                     - ZSurfaceAwt.this.currentInsets.top
                     - ZSurfaceAwt.this.currentInsets.bottom;
                  ZSurfaceAwt.this.setSize(w
                     / ZSurfaceAwt.this.windowScaleFactor, h
                        / ZSurfaceAwt.this.windowScaleFactor);

                  ZSurfaceAwt.this.setLocation(x
                     - ZSurfaceAwt.this.currentInsets.left, y
                        - ZSurfaceAwt.this.currentInsets.top);
               }
            }
         }

      });
   }

   public static int awtToProcessingMouseButton ( final int button ) {

      switch ( button ) {
         case java.awt.event.MouseEvent.BUTTON1:
            return PConstants.LEFT;

         case java.awt.event.MouseEvent.BUTTON2:
            return PConstants.CENTER;

         case java.awt.event.MouseEvent.BUTTON3:
            return PConstants.RIGHT;

         default:
            return 0;
      }
   }

   /** Get the bounds rectangle for all displays. */
   static Rectangle getDisplaySpan ( ) {

      final Rectangle bounds = new Rectangle();
      final GraphicsEnvironment environment = GraphicsEnvironment
         .getLocalGraphicsEnvironment();
      for ( final GraphicsDevice device : environment.getScreenDevices() ) {
         for ( final GraphicsConfiguration config : device
            .getConfigurations() ) {
            Rectangle2D.union(bounds, config.getBounds(), bounds);
         }
      }
      return bounds;
   }

   private static boolean dockIconSpecified ( ) {

      final List < String > jvmArgs = ManagementFactory.getRuntimeMXBean()
         .getInputArguments();
      for ( final String arg : jvmArgs ) {
         if ( arg.startsWith("-Xdock:icon") ) { return true; }
      }
      return false;
   }

   public class SmoothCanvas extends Canvas {
      private final Dimension newSize = new Dimension(0, 0);
      private Dimension oldSize = new Dimension(0, 0);

      public Frame getFrame ( ) { return ZSurfaceAwt.this.frame; }

      @Override
      public Dimension getMaximumSize ( ) {

         return ZSurfaceAwt.this.frame.isResizable() ? super.getMaximumSize()
            : this.getPreferredSize();
      }

      @Override
      public Dimension getMinimumSize ( ) { return this.getPreferredSize(); }

      @Override
      public Dimension getPreferredSize ( ) {

         return new Dimension(ZSurfaceAwt.this.sketchWidth,
            ZSurfaceAwt.this.sketchHeight);
      }

      @Override
      public void paint ( final Graphics screen ) {

         ZSurfaceAwt.this.render();
      }

      @Override
      public void update ( final Graphics g ) {

         this.paint(g);
      }

      @Override
      public void validate ( ) {

         super.validate();
         this.newSize.width = this.getWidth();
         this.newSize.height = this.getHeight();
         if ( !this.oldSize.equals(this.newSize) ) {
            this.oldSize = this.newSize;
            ZSurfaceAwt.this.sketch.setSize(this.newSize.width
               / ZSurfaceAwt.this.windowScaleFactor, this.newSize.height
                  / ZSurfaceAwt.this.windowScaleFactor);
            ZSurfaceAwt.this.render();
         }
      }

      private static final long serialVersionUID = -306572426632277042L;

   }

}
