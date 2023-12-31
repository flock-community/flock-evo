

export class ColorService {

  private static idToColorMap: Map<string, string> = new Map();

  public static getColorById(id: string) {
    if (!this.idToColorMap.has(id)) {
      this.idToColorMap.set(id, 'hsla(' + (Math.random() * 360) + ', 100%, 50%, 1)')
    }
    return this.idToColorMap.get(id);
  }
}
