// For authoring Nightwatch tests, see
// https://nightwatchjs.org/guide

module.exports = {
  'default e2e tests': browser => {
    browser
      .init()
      .assert.containsText('h1', 'Application de traitement d\'images')
      .end()
  },
}
