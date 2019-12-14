import { Selector } from './selector'

export class BrowserClient {
    constructor(public browserObject: WebdriverIO.BrowserObject) {
    }

    findElement(selector: Selector): Promise<WebdriverIOAsync.Element> {
        return this.browserObject.$(selector.toSelectorString())
    }

    findElementArray(selector: Selector): Promise<WebdriverIOAsync.ElementArray> {
        return this.browserObject.$$(selector.toSelectorString())
    }
}