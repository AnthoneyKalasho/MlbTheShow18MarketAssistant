$(document).ready(function () {
    'use strict';
    // "Market prices on this page are refreshed hourly." Not anymore!!! :D
    $('.marketplace-filter-disclaimer').remove();
    var tableHTML = '<table class="sortable" width= 100%; font-size: 15px;"><thead><tr><th>Name</th><th>Buy Now</th><th>Sell Now</th><th>Profit(After Tax)</th><th>Difference% (After Tax)</th><th>Bought/Sold in the past hour</th></thead></tr><tbody>';
    var objectArray = [];
    $('.marketplace-filter-item').each(function () {
        var name = $(this).find('.marketplace-filter-item-name > a').text();
        var url = $(this).find('.marketplace-filter-item-name > a').attr('href');
        var row = {
            url: url,
            name: name,
        };
        objectArray.push(row);
    });
    // Make room for MLB The Show 18 Market Assistant supertable
    $('.marketplace-filter-list').remove();
    $(objectArray).each( function () {
        tableHTML += '<tr><td class="nameRow"><a href="' + $(this)[0].url + '">' + $(this)[0].name + '</a></td><td class="buyNowRow">-</td><td class="sellNowRow">-</td><td class="datRow">-</td><td class="pdRow">-</td><td class="soldInHourRow">-</td></tr>';
    });
    tableHTML += '</tbody></table>';
    $('.menu-pagination').before('<script type="text/javascript" src="https://www.kryogenix.org/code/browser/sorttable/sorttable.js">');
    $('.menu-pagination').before(tableHTML);
    $('.sortable > tbody  > tr').each(function () {
        var mBuyNowPrice;
        var mSellNowPrice;
        var mSoldInHour = 0;
        var rowURL = $(this).find('.nameRow > a').attr('href');
        var buyNowRow = $(this).find('.buyNowRow');
        var sellNowRow = $(this).find('.sellNowRow');
        var datRow = $(this).find('.datRow');
        var pdRow = $(this).find('.pdRow');
        var soldInHourRow = $(this).find('.soldInHourRow');
        soldInHourRow.html(0);
        $.ajax({url: rowURL
               }).done(function (data) {
            var parsedData = $(jQuery.parseHTML(data));
            var buyNowButtonContainer = parsedData.find('.marketplace-card-buy-orders > .marketplace-card-create-forms > .marketplace-card-order-now')[0];
            // If somebody is offering to sell this item, get price
            if(buyNowButtonContainer!=null){mBuyNowPrice = buyNowButtonContainer.innerText.replace(/\D/g, '');}
            // Don't have to do this but it makes console errors go away
            if(mBuyNowPrice != null){buyNowRow.html(mBuyNowPrice);}
            var sellNowButtonContainer = parsedData.find('.marketplace-card-sell-orders > .marketplace-card-create-forms > .marketplace-card-order-now')[0];
            // If somebody is offering to buy this item, get price
            if(sellNowButtonContainer!=null){mSellNowPrice = sellNowButtonContainer.innerText.replace(/\D/g, '');}
            // Don't have to do this but it makes console errors go away
            if(mSellNowPrice != null){sellNowRow.html(mSellNowPrice);}
            parsedData.find('.completed-order').each(function () {
                var thisDate = getDate($(this).find('.date').text());
                var OneHourAgo = new Date(Date.now());
                OneHourAgo.setHours(OneHourAgo.getHours() - 1);
                // These come in descending order
                if (thisDate > OneHourAgo) {
                    mSoldInHour++;
                }
                else {
                    // Stop as soon as you find one that is more than an hour ago
                    return false;
                }
            });
            soldInHourRow.html(mSoldInHour);
            var mDifAfterTax;
            // mBuyNowPrice can be null if nobody is offering to sell this item
            // mSellNowPrice can be null if nobody is offering to buy this item
            if(mBuyNowPrice != null && mSellNowPrice !=null)
            {
                mDifAfterTax = (mBuyNowPrice - (mBuyNowPrice * 0.1)) - mSellNowPrice;
                mDifAfterTax = precisionRound(mDifAfterTax, 2);
                datRow.html(mDifAfterTax);
                var mPercentDiff = (mDifAfterTax / mSellNowPrice) * 100;
                mPercentDiff = precisionRound(mPercentDiff, 2);
                pdRow.html(mPercentDiff+'%');
            }
        });
    });
});

function getDate(dateString)
{
  if(dateString.substring(20,21)=='P')
  {
    var hour = parseInt(dateString.substring(15,17));
    hour += 12;
    return new Date(dateString.slice(0,15) + hour + dateString.slice(17,20));
  }
}

function precisionRound(number, precision) {
    var factor = Math.pow(10, precision);
    return Math.round(number * factor) / factor;
}