package ru.emkn.kotlin

import java.io.File
import java.lang.Exception

interface memoryAlgo {
    fun query(page: Int): Int
}

class FIFO(private val n: Int, private val m: Int) : memoryAlgo {
    private var ind = 0
    private val have = mutableSetOf<Int>()
    private val cards = Array(m) { -1 }

    override fun query(page: Int): Int {
        if (!have.add(page))
            return -1
        else {
            val ans = ind
            have.remove(cards[ind])
            cards[ind] = page
            ind = (ind + 1) % m
            return ans
        }
    }
}

class LRU(private val n: Int, private val m: Int) : memoryAlgo {
    private var time = 0
    private val timePage = mutableMapOf<Int, Int>()
    private val pageTime = mutableMapOf<Int, Int>()
    private val pagePos = mutableMapOf<Int, Int>()
    private val have = sortedSetOf<Int>()

    override fun query(page: Int): Int {
        var res = -1
        if (pageTime.containsKey(page) && have.contains(pageTime[page]!!))
            have.remove(pageTime[page]!!)
        else if (have.size < m) {
            pagePos[page] = have.size
            res = pagePos[page]!!
        } else {
            val optimal = have.first()
            have.remove(have.first())
            pagePos[page] = pagePos[timePage[optimal]!!]!!
            res = pagePos[page]!!
        }
        have.add(++time)
        timePage[time] = page
        pageTime[page] = time
        return res
    }
}

class OPT(private val n: Int, private val m: Int, private val order: List<Int>) : memoryAlgo {
    private val pairComparator = Comparator<Pair<Int, Int>> { a, b -> a.first.compareTo(b.first) }
    private val have = sortedSetOf(pairComparator)
    private val nxt = Array(n) { mutableListOf<Int>() }
    private var index = 0
    private val it = Array(n) { 0 }
    private val pos = Array(n) { 0 }

    init {
        order.forEachIndexed { index, el -> nxt[el].add(index) }
        for (el in order)
            nxt[el].add(order.size)
    }

    override fun query(page: Int): Int {
        if(order[index] != page)
            throw Exception("page number doesn't match current order page number")
        when {
            (have.contains(Pair(nxt[page][it[page]], 0))) -> {
                have.remove(Pair(index, 0))
                have.add(Pair(nxt[page][++it[page]], page))
                index++
                return -1
            }
            (have.size < m) -> {
                pos[page] = have.size
                have.add(Pair(nxt[page][++it[page]], page))
                index++
                return pos[page]
            }
            else -> {
                val optimal = have.last()
                have.remove(have.last())
                have.add(Pair(nxt[page][++it[page]], page))
                pos[page] = pos[optimal.second]
                index++
                return pos[page]
            }
        }
    }
}

fun main(args: Array<String>) {
    try {
        val reader = File(args[0]).useLines { it.toList() }

        val n = reader[0].split(" ")[0].toInt()
        val m = reader[0].split(" ")[1].toInt()
        val order = reader[1].split(" ").map { it.toInt() - 1 }

        val Fifo = FIFO(n, m)
        val resFifo = order.map { Fifo.query(it) }
        println("FIFO:")
        println(resFifo)
        println("Ответов второго типа: ${resFifo.count { it != -1 }}")
        val Lru = LRU(n, m)
        val resLru = order.map { Lru.query(it) }
        println("LRU:")
        println(resLru)
        println("Ответов второго типа: ${resLru.count { it != -1 }}")
        val Opt = OPT(n, m, order)
        val resOpt = order.map { Opt.query(it) }
        println("OPT:")
        println(resOpt)
        println("Ответов второго типа: ${resOpt.count { it != -1 }}")
    } catch (e: NumberFormatException) {
        print("Неверный формат ввода")
    }
}
