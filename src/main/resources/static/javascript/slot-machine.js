// Copyright (c) 2024 by Morgan (https://codepen.io/mog13/pen/VRBgNQ)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
//
//     The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
//
//     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

    let reelContents = ["" ,"💧", "☀️", "🍄", "🌶️", "🌾"];
    let reelLength = 3;
    let reelContainers = document.querySelectorAll(".reel-container");
    let spinningReels = [];
    let spinning = false;
    let reelDelay = 100;
    let money = 100;            //Todo set to blooms amount
    let moneyToAdd = 0;     //Ensure this is updated to be model attribute "amountWon" at appropriate time
    let audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    let masterVolume = audioCtx.createGain();
    masterVolume.gain.setValueAtTime(0.05, audioCtx.currentTime);
    masterVolume.connect(audioCtx.destination);
    let getReelItem = (reelIndex) => {
        let slotList = slots[reelIndex];
        let index = slotList.shift(); // Get the next index from the slot list
        let newReel = document.createElement("div");
        // newReel.innerHTML = reelContents[Math.floor(Math.random() * reelContents.length)];
        newReel.innerHTML = reelContents[index]; // Use the index to get the correct content
        newReel.classList.add("reel-item");
        setTimeout(() => {
            newReel.classList.add("active");
        }, 0);
        return newReel;
    };

    let spinStarted = false; // This is very important and checks if you have spun already

    let startSpin = () => {
        if (!spinStarted && !spinning && money > 0) {
            document.querySelectorAll(".prize-item.active").forEach(s => {
                s.classList.remove("active");
            });
            updateMoney(-1);
            setChange(-1);
            spinningReels = [0, 1, 2, 3, 4];  // Include all reels at once
            spinning = true;
            spinUpdate(11);     //This works out as working through the 15 increments of the reel

            spinStarted = true; // Updates flag to show user has spun once
        } else if (spinStarted) {
            location.reload(); // Reload the page if spin has already started
        }
    };
    let spinUpdate = spinsLeft => {
        spinningReels.forEach(n => {
            moveReel(n);
        });
        if (spinsLeft > 0) {
            setTimeout(() => {
                spinUpdate(spinsLeft - 1);
            }, reelDelay);
        } else {
            spinning = false;
            findWins();
        }
    };
    let moveReel = (reelIndex) => {
        let selectedReel = reelContainers[reelIndex];
        selectedReel.prepend(getReelItem(reelIndex));
        if (selectedReel.children.length > reelLength) {
            selectedReel.lastElementChild.classList.add("deactivate");
            setTimeout(() => {
                selectedReel.removeChild(selectedReel.lastElementChild);
            }, reelDelay);
        }
    };
    let updateMoney = change => {
        money += change;
        document.querySelector("#money").innerText = money;
    };
    let setChange = change => {
        let changes = document.querySelector(".changes");
        let newChange = document.createElement("div");
        newChange.innerHTML = change > 0 ? `+${change}` : change;
        newChange.classList.add("change");
        if (change < 0) newChange.classList.add("negative");
        changes.prepend(newChange);
        if (changes.children.length > 6) {
            changes.removeChild(changes.lastElementChild);
        }
    };
    let playWinChime = amount => {
        let clampedAm = amount > 20 ? 20 : amount;
        playNote(400 + 100 * (20 - clampedAm), 0.05, "sine");
        if (--amount > 0) setTimeout(() => {
            playWinChime(amount);
        }, 70);
    };

    let updateSymbolCounts = (symbols, reel) => {
        let symbolTop = reel.children[0].innerText;
        symbols.top[symbolTop] = (symbols.top[symbolTop] || 0) + 1; //increment count of this symbol in top row

        let symbolMiddle = reel.children[1].innerText;
        symbols.middle[symbolMiddle] = (symbols.middle[symbolMiddle] || 0) + 1;

        let symbolBottom = reel.children[2].innerText;
        symbols.bottom[symbolBottom] = (symbols.bottom[symbolBottom] || 0) + 1;
    };

    let getHighestMatch = (symbols) => {
        let highest = { count: 2, emoji: '🌶️' };
        for (let emoji in symbols) {
            if (symbols[emoji] > highest.count) {
                highest.count = symbols[emoji];
                highest.emoji = emoji;
            }
        }
        return highest;
    };

    let findHighestMatchingRow = (top, middle, bottom) => {
        let highestTop = getHighestMatch(top);
        let highestMiddle = getHighestMatch(middle);
        let highestBottom = getHighestMatch(bottom);

        if (highestTop.count > highestMiddle.count && highestTop.count > highestBottom.count) {
            return { count: highestTop.count, emoji: highestTop.emoji, row: 0 };
        } else if (highestMiddle.count >= highestTop.count && highestMiddle.count >= highestBottom.count) {
            return { count: highestMiddle.count, emoji: highestMiddle.emoji, row: 1 };
        } else {
            return { count: highestBottom.count, emoji: highestBottom.emoji, row: 2 };
        }
    };
    let findWins = () => {
        let symbols = { top: {}, middle: {}, bottom: {} };
        reelContainers.forEach(reel => updateSymbolCounts(symbols, reel));
        let { count, emoji, row } = findHighestMatchingRow(symbols.top, symbols.middle, symbols.bottom);
        if (count > 2) {
            win(count, emoji, row);
        }

        //TODO add popup/ message for unsuccessful spin

    };
    let win = (amountMatching, symbol, rowNumber) => {
        reelContainers.forEach(reel => {
            if (reel.children[rowNumber].innerText === symbol) reel.children[rowNumber].classList.add("win");
        });
        let winAmount = reelContents.indexOf(symbol);
        playWinChime(winAmount);
        if (amountMatching === 4) winAmount *= 10;
        if (amountMatching === 5) winAmount *= 100;
        setChange(winAmount);
        addToMoney(winAmount);
    };
    let addToMoney = (amount, speed) => {
        let changeAmount = Math.ceil(amount / 2);
        updateMoney(changeAmount);
        let remainder = amount - changeAmount;
        if (!speed) speed = 101;
        speed -= 5;
        if (speed < 10) speed = 10;
        if (remainder) setTimeout(() => {
            addToMoney(remainder);
        }, speed);
    };
    function playNote(freq, dur, type) {
        if (!freq) freq = 1000;
        if (!dur) dur = 1;
        if (!type) type = "square";
        return new Promise(res => {
            let oscillator = audioCtx.createOscillator();
            oscillator.type = type;
            oscillator.frequency.setValueAtTime(freq, audioCtx.currentTime); // value in hertz
            oscillator.connect(masterVolume);
            oscillator.start();
            oscillator.stop(audioCtx.currentTime + dur);
            oscillator.onended = res;
        });
    }

    //fills reels
    reelContainers.forEach((reel, i) => {
        for (let n = 0; n < reelLength; n++) {
            moveReel(i);
        }
    });
    let addToPrizeTable = (combo, amount, target) => {
        let pt = document.querySelector(`.prize-table .${target}`);
        let prize = document.createElement("div");
        prize.innerHTML = `${combo}: ${amount}฿`;
        prize.classList.add("prize-item");
        prize.setAttribute("win-attr", combo.replace(/[-❔]/g, ""));
        pt.append(prize);
    };

    let getComboType = (target) => {
        switch (target) {
            case "triples":
                return "3 of a kind";
            case "quadruples":
                return "4 of a kind";
            case "quintuples":
                return "5 of a kind";
            default:
                return "";
        }
    };

    let setPrizeTableHeaders = (target) => {
        let pt = document.querySelector(`.prize-table .${target}`);
        let heading = document.createElement("div");
        let comboType = getComboType(target);
        heading.innerHTML = comboType;
        pt.append(heading);
    }

    //fill prize table
    setPrizeTableHeaders("triples");
    setPrizeTableHeaders("quadruples");
    setPrizeTableHeaders("quintuples");


    reelContents.forEach((symbol, index) => {
        if (index !== 0) {
            addToPrizeTable(`${symbol}`, index, "triples");
            addToPrizeTable(`${symbol}`, (index) * 10, "quadruples");
            addToPrizeTable(`${symbol}`, (index) * 100, "quintuples");
        }
    });

    //Modal functionality - can possibly refactor to bootstrap but was facing too many conflicts
    var modal = document.getElementById("prizeModal");
    var btn = document.getElementById("infoButton");
    var span = document.getElementsByClassName("close")[0];

    btn.onclick = function() {
        modal.style.display = "block";
    }

    span.onclick = function() {
        modal.style.display = "none";
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }