package camzup.core;

/**
 * The order in which affine transformations -- translation, rotation,
 * scale -- are applied.
 */
public enum TransformOrder {

  /**
   * Rotation, Scale, Translation
   */
  RST ( "[ \"ROTATION\", \"SCALE\", \"TRANSLATION\" ]" ),

  /**
   * Rotation, Translation, Scale
   */
  RTS ( "[ \"ROTATION\", \"TRANSLATION\", \"SCALE\" ]" ),

  /**
   * Scale, Rotation, Translation
   */
  SRT ( "[ \"SCALE\", \"ROTATION\", \"TRANSLATION\" ]" ),

  /**
   * Scale, Translation, Rotation
   */
  STR ( "[ \"SCALE\", \"TRANSLATION\", \"ROTATION\" ]" ),

  /**
   * Translation, Rotation, Scale
   */
  TRS ( "[ \"TRANSLATION\", \"ROTATION\", \"SCALE\" ]" ),

  /**
   * Translation, Scale, Rotation
   */
  TSR ( "[ \"TRANSLATION\", \"SCALE\", \"ROTATION\" ]" );

  /**
   * The long-form name displayed in the console.
   */
  private final String printName;

  /**
   * The default constructor
   *
   * @param name the long-form name
   */
  private TransformOrder ( final String name ) { this.printName = name; }

  /**
   * Gets the print name of this transform order.
   *
   * @return the print name
   */
  public String getPrintName ( ) { return this.printName; }

  /**
   * Returns the reverse of a given order. For example, RTS returns STR.
   *
   * @param order the order
   * @return the reverse order
   */
  public static TransformOrder reverse ( final TransformOrder order ) {

    switch ( order ) {
      case RST:
        return TransformOrder.TSR;
      case RTS:
        return TransformOrder.STR;
      case SRT:
        return TransformOrder.TRS;
      case STR:
        return TransformOrder.RTS;
      case TRS:
        return TransformOrder.SRT;
      case TSR:
      default:
        return TransformOrder.RST;
    }
  }
}
