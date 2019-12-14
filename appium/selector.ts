export class Selector {
    private itemArray: Array<string> = ['new UiSelector()']

    text(value: string): Selector {
        this.itemArray.push(`text("${value}")`)
        return this
    }

    className(value: string): Selector {
        this.itemArray.push(`className("${value}")`)
        return this
    }

    clickable(value: boolean): Selector {
        this.itemArray.push(`clickable(${value})`)
        return this
    }

    resourceIdMatches(value: string): Selector {
        this.itemArray.push(`resourceIdMatches("${value}")`)
        return this
    }

    toSelectorString(): string {
        return `android=${this.itemArray.join('.')}`
    }
}
