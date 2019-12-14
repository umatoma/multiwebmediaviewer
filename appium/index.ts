import path from 'path'
import { expect } from 'chai'
import { remote } from 'webdriverio'
import { Selector } from './selector'
import { BrowserClient } from './browserClient'

const config: WebdriverIO.RemoteOptions = {
    port: 4723,
    capabilities: {
        platformName: 'Android',
        deviceName: 'Android',
        app: path.resolve(__dirname, '../app/build/outputs/apk/debug/app-debug.apk'),
        appPackage: 'io.github.umatoma.multiwebmediaviewer',
        appActivity: '.view.start.StartActivity',
        automationName: 'uiautomator2'
    },
    logLevel: 'warn'
}

describe('Android UI tests', function () {

    this.timeout(60000)

    let client: BrowserClient

    before(async () => {
        client = new BrowserClient(await remote(config))
    })

    after(async () => {
        if (client) {
            await client.browserObject.deleteSession()
        }
    })

    describe('HomeActivity', () => {
        it('should display sign in button for Hatena', async () => {
            const navBtn = await client.findElement(new Selector().resourceIdMatches('.+menuHatenaEntryList'))
            await navBtn.click()

            const btnSignIn = await client.findElement(new Selector().text('はてなID 認証'))
            expect(await btnSignIn.isExisting()).to.be.equal(true)
        })

        it('should display sign in button for Feedly', async () => {
            const navBtn = await client.findElement(new Selector().resourceIdMatches('.+menuFeelyEntryList'))
            await navBtn.click()

            const btnSignIn = await client.findElement(new Selector().text('FEEDLY 認証'))
            expect(await btnSignIn.isExisting()).to.be.equal(true)
        })

        it('should display settings view', async () => {
            const navBtn = await client.findElement(new Selector().resourceIdMatches('.+menuSettings'))
            await navBtn.click()

            // TBD
        })
    })
})
