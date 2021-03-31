package camzup.pfriendly;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URL;

import javax.swing.JFrame;

import java.util.ArrayList;
import java.util.Iterator;

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

/**
 * An AWT-backed surface manually ported from Processing 4 alpha to fix
 * bugs with setIcon and mouseReleased. This surface is generated by the
 * {@link YupJ2} renderer.
 */
public class ZSurfaceAwt extends PSurfaceNone {
   protected Canvas canvas;
   protected Insets currentInsets = new Insets(0, 0, 0, 0);
   protected int cursorType = PConstants.ARROW;
   protected boolean cursorVisible = true;
   protected GraphicsDevice displayDevice;
   protected Frame frame;
   protected ArrayList < Image > iconImages;
   protected Cursor invisibleCursor;
   protected Rectangle screenRect;
   protected int sketchHeight;
   protected int sketchWidth;
   protected int windowScaleFactor;

   /**
    * The default constructor.
    *
    * @param graphics the graphics renderer
    */
   public ZSurfaceAwt ( final PGraphics graphics ) {

      /* Copied from PSurfaceAWT on 03/16/2021. JB. */

      super(graphics);

      this.canvas = new SmoothCanvas();
      this.canvas.setFocusTraversalKeysEnabled(false);
      // this.canvas.setMinimumSize(new Dimension(128, 128));
      // this.canvas.setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());

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

   /**
    * Creates and returns a new AnimationThread. This thread calls the
    * sketch's handle draw function, then calls render.
    */
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

   /**
    * Returns an AWT {@link Canvas} cast to an object.
    */
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
         final int devicesLen = devices.length;

         if ( displayNum <= devicesLen ) {
            this.displayDevice = devices[displayNum - 1];
         } else {
            System.err.format("Display %d does not exist, "
               + "using the default display instead.%n", displayNum);
            for ( int i = 0; i < devicesLen; ++i ) {
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

      // TODO: To make SmoothCanvas static, you need to set its sketchWidth and
      // sketchHeight as well.
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
      if ( fullScreen ) { this.frame.invalidate(); }

      this.frame.setResizable(false);
      // this.frame.setMinimumSize(new Dimension(128, 128));
      // this.frame.setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());

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
   public PImage loadImage ( final String path, final Object... args ) {

      return ShimAWT.loadImage(this.sketch, path, args);
   }

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

      final int wContent = Math.max(this.sketchWidth,
         PSurface.MIN_WINDOW_WIDTH);
      final int hContent = Math.max(this.sketchHeight,
         PSurface.MIN_WINDOW_HEIGHT);

      if ( this.sketch.sketchFullScreen() ) { this.setFullFrame(); }

      if ( !this.sketch.sketchFullScreen() ) {
         if ( location != null ) {
            this.frame.setLocation(location[0], location[1]);

         } else if ( editorLocation != null ) {
            final int locationX = editorLocation[0] - 20;
            if ( locationX - window.width > 10 ) {
               this.frame.setLocation(locationX - window.width,
                  editorLocation[1]);
            } else {
               this.frame.setLocation( ( this.sketch.displayWidth
                  - window.width ) / 2, ( this.sketch.displayHeight
                     - window.height ) / 2);
            }
         } else {
            this.setFrameCentered();
         }
         final Point frameLoc = this.frame.getLocation();
         if ( frameLoc.y < 0 ) { this.frame.setLocation(frameLoc.x, 30); }
      }

      this.canvas.setBounds( ( wContent - this.sketchWidth ) / 2, ( hContent
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

   /**
    * Sets the cursor to an image with a given hot spot.
    *
    * @param img the Processing image
    * @param x   the hot spot x
    * @param y   the hot spot y
    *
    * @see Toolkit#createCustomCursor(Image, Point, String)
    * @see Canvas#setCursor(Cursor)
    */
   @Override
   public void setCursor ( final PImage img, final int x, final int y ) {

      final Dimension cursorSize = Toolkit.getDefaultToolkit()
         .getBestCursorSize(img.width, img.height);
      if ( cursorSize.width == 0 || cursorSize.height == 0 ) { return; }
      final Cursor cursor = this.canvas.getToolkit().createCustomCursor(YupJ2
         .convertPImageToNative(img), new Point(x, y), "custom");
      this.canvas.setCursor(cursor);
      this.cursorVisible = true;
   }

   /**
    * Sets the applet icon to an image.
    *
    * @param img the Processing image
    *
    * @see Frame#setIconImage(Image)
    */
   @Override
   public void setIcon ( final PImage img ) {

      final Image awtImage = YupJ2.convertPImageToNative(img);

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

   /**
    * Sets the applet to be resizable. Not supported with this surface.
    *
    * @param resizable true or false
    */
   @Override
   public void setResizable ( final boolean resizable ) {

      /*
       * Unsupported: https://github.com/processing/processing4/issues/186 .
       */
      PApplet.showMethodWarning("setResizable");
      // if ( this.frame != null ) { this.frame.setResizable(resizable); }
   }

   @Override
   public void setSize ( final int w, final int h ) {

      /*
       * Increasing the minimum bound here does not prevent resizing window to
       * 0, 0 in the issues https://github.com/processing/processing4/issues/186
       * https://github.com/processing/processing/issues/5052 .
       */
      final int wid = w < 2 ? 2 : w;
      final int high = h < 2 ? 2 : h;

      if ( wid == this.sketch.width && high == this.sketch.height
         && ( this.frame == null || this.currentInsets.equals(this.frame
            .getInsets()) ) ) {
         return;
      }

      this.sketchWidth = wid * this.windowScaleFactor;
      this.sketchHeight = high * this.windowScaleFactor;

      if ( this.frame != null ) { this.setFrameSize(); }
      this.setCanvasSize();
      this.sketch.setSize(wid, high);
      this.graphics.setSize(wid, high);
   }

   @Override
   public void setTitle ( final String title ) {

      this.frame.setTitle(title);
      if ( this.cursorVisible && PApplet.platform == PConstants.MACOS
         && this.cursorType != PConstants.ARROW ) {
         this.hideCursor();
         this.showCursor();
      }
   }

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

   /**
    * Converts {@link java.awt.event.KeyEvent}s into
    * {@link processing.event.KeyEvent}s.
    *
    * @param event the AWT event
    */
   protected void nativeKeyEvent ( final java.awt.event.KeyEvent event ) {

      int peAction;
      final int eventid = event.getID();
      switch ( eventid ) {
         case java.awt.event.KeyEvent.KEY_TYPED:
            peAction = KeyEvent.TYPE;
            break;

         case java.awt.event.KeyEvent.KEY_PRESSED:
            peAction = KeyEvent.PRESS;
            break;

         case java.awt.event.KeyEvent.KEY_RELEASED:
            peAction = KeyEvent.RELEASE;
            break;

         default:
            peAction = 0;
      }

      this.sketch.postEvent(new KeyEvent(event, event.getWhen(), peAction, event
         .getModifiersEx(), event.getKeyChar(), event.getKeyCode()));
   }

   /**
    * Converts {@link java.awt.event.MouseEvent}s into
    * {@link processing.event.MouseEvent}s.
    *
    * @param event the AWT event
    */
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

      int peAction;
      final int eventid = nativeEvent.getID();
      switch ( eventid ) {
         case java.awt.event.MouseEvent.MOUSE_CLICKED:
            peAction = MouseEvent.CLICK;
            peButton = ZSurfaceAwt.awtToProcessingMouseButton(nativeEvent
               .getButton());
            break;

         case java.awt.event.MouseEvent.MOUSE_PRESSED:
            peAction = MouseEvent.PRESS;
            break;

         case java.awt.event.MouseEvent.MOUSE_RELEASED:
            peAction = MouseEvent.RELEASE;
            peButton = ZSurfaceAwt.awtToProcessingMouseButton(nativeEvent
               .getButton());
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

         case java.awt.event.MouseEvent.MOUSE_DRAGGED:
            peAction = MouseEvent.DRAG;
            break;

         case java.awt.event.MouseEvent.MOUSE_WHEEL:
            peAction = MouseEvent.WHEEL;
            peCount = ( ( MouseWheelEvent ) nativeEvent ).getWheelRotation();
            break;

         default:
            peAction = 0;
      }

      this.sketch.postEvent(new MouseEvent(nativeEvent, nativeEvent.getWhen(),
         peAction, modifiers, nativeEvent.getX() / this.windowScaleFactor,
         nativeEvent.getY() / this.windowScaleFactor, peButton, peCount));
   }

   protected synchronized void render ( ) {

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
               this.iconImages = new ArrayList <>(8);
               final int[] sizes = { 16, 32, 48, 64, 128, 256, 512 };
               final int sizesLen = sizes.length;
               for ( int i = 0; i < sizesLen; ++i ) {
                  final String path = new StringBuilder(18).append(
                     "/icon/icon-").append(sizes[i]).append(".png").toString();
                  this.iconImages.add(Toolkit.getDefaultToolkit().getImage(
                     PApplet.class.getResource(path)));
               }
            }
            frame.setIconImages(this.iconImages);

         } catch ( final Exception e ) {
            e.printStackTrace();
         }

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

      final int wContent = Math.max(this.sketchWidth,
         PSurface.MIN_WINDOW_WIDTH);
      final int hContent = Math.max(this.sketchHeight,
         PSurface.MIN_WINDOW_HEIGHT);

      this.canvas.setBounds( ( wContent - this.sketchWidth ) / 2, ( hContent
         - this.sketchHeight ) / 2, this.sketchWidth, this.sketchHeight);
   }

   private void setFrameCentered ( ) {

      this.frame.setLocation(this.screenRect.x + ( this.screenRect.width
         - this.sketchWidth ) / 2, this.screenRect.y + ( this.screenRect.height
            - this.sketchHeight ) / 2);
   }

   private Dimension setFrameSize ( ) {

      this.frame.addNotify();
      this.currentInsets = this.frame.getInsets();
      final int wWindow = Math.max(this.sketchWidth, PSurface.MIN_WINDOW_WIDTH)
         + this.currentInsets.left + this.currentInsets.right;
      final int hWindow = Math.max(this.sketchHeight,
         PSurface.MIN_WINDOW_HEIGHT) + this.currentInsets.top
         + this.currentInsets.bottom;

      this.frame.setSize(wWindow, hWindow);
      return new Dimension(wWindow, hWindow);
   }

   private void setFullFrame ( ) {

      PApplet.hideMenuBar();

      this.frame.removeNotify();
      this.frame.setUndecorated(true);
      this.frame.addNotify();

      this.frame.setBounds(this.screenRect);
   }

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
               final Frame fr = ( Frame ) e.getComponent();
               if ( fr.isVisible() ) {
                  final Insets insets = ZSurfaceAwt.this.currentInsets;
                  final int left = insets.left;
                  final int top = insets.top;
                  final int right = insets.right;
                  final int bottom = insets.bottom;

                  final Dimension windowSize = fr.getSize();
                  final int w = windowSize.width - left - right;
                  final int h = windowSize.height - top - bottom;

                  ZSurfaceAwt.this.setSize(w
                     / ZSurfaceAwt.this.windowScaleFactor, h
                        / ZSurfaceAwt.this.windowScaleFactor);

                  ZSurfaceAwt.this.setLocation(fr.getX(), fr.getY());
               }
            }
         }

      });
   }

   /**
    * Converts an AWT mouse button, such as
    * {@link java.awt.event.MouseEvent#BUTTON1}, to a Processing mouse button.
    * For use in cases where a mouse is released or clicked, and the AWT
    * masking method doesn't properly recognize which button.
    *
    * @param button the AWT button
    *
    * @return the Processing button
    */
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

   static Rectangle getDisplaySpan ( ) {

      final Rectangle bounds = new Rectangle();
      final GraphicsDevice[] devices = GraphicsEnvironment
         .getLocalGraphicsEnvironment().getScreenDevices();
      final int devicesLen = devices.length;
      for ( int i = 0; i < devicesLen; ++i ) {
         final GraphicsConfiguration[] configs = devices[i].getConfigurations();
         final int configsLen = configs.length;
         for ( int j = 0; j < configsLen; ++j ) {
            Rectangle2D.union(bounds, configs[j].getBounds(), bounds);
         }
      }
      return bounds;
   }

   private static boolean dockIconSpecified ( ) {

      final Iterator < String > jvmItr = ManagementFactory.getRuntimeMXBean()
         .getInputArguments().iterator();
      while ( jvmItr.hasNext() ) {
         if ( jvmItr.next().startsWith("-Xdock:icon") ) { return true; }
      }
      return false;
   }

   public class SmoothCanvas extends Canvas {
      protected final Dimension newSize = new Dimension(0, 0);
      protected Dimension oldSize = new Dimension(0, 0);

      public Frame getFrame ( ) { return ZSurfaceAwt.this.frame; }

      @Override
      public Dimension getMaximumSize ( ) {

         // return ZSurfaceAwt.this.frame.isResizable() ? super.getMaximumSize()
         // : this.getPreferredSize();
         return super.getMaximumSize();
      }

      @Override
      public Dimension getMinimumSize ( ) {

         // return this.getPreferredSize();
         return super.getMinimumSize();
      }

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
