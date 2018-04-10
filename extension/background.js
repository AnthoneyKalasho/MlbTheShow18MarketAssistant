chrome.browserAction.onClicked.addListener(function (activeTab) {
    chrome.tabs.create({ url: 'https://mlb18.theshownation.com/community_market' });
});