-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 20, 2023 at 04:43 AM
-- Server version: 8.1.0
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `imsdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `apptable`
--

CREATE TABLE `apptable` (
  `appID` int NOT NULL,
  `countUsers` int NOT NULL,
  `currentUser` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `apptable`
--

INSERT INTO `apptable` (`appID`, `countUsers`, `currentUser`) VALUES
(1, 1, 'superuser.wan');

-- --------------------------------------------------------

--
-- Table structure for table `categorytable`
--

CREATE TABLE `categorytable` (
  `categoryID` int NOT NULL,
  `categoryName` varchar(45) NOT NULL,
  `dateCreated` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `categorytable`
--

INSERT INTO `categorytable` (`categoryID`, `categoryName`, `dateCreated`) VALUES
(1, 'Drinks', '2023-12-16'),
(2, 'Foods', '2023-12-16');

-- --------------------------------------------------------

--
-- Table structure for table `inventorytable`
--

CREATE TABLE `inventorytable` (
  `productID` int NOT NULL,
  `Category` varchar(45) NOT NULL,
  `ProductName` varchar(45) NOT NULL,
  `Description` varchar(45) NOT NULL,
  `Quantity` int NOT NULL,
  `RetailPrice` decimal(10,0) NOT NULL,
  `DateOfPurchase` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `inventorytable`
--

INSERT INTO `inventorytable` (`productID`, `Category`, `ProductName`, `Description`, `Quantity`, `RetailPrice`, `DateOfPurchase`) VALUES
(1, 'Foods', 'Chickens', 'Best', 1, 121, '2023-12-11'),
(2, 'Foods', 'Hotdog', 'Good', 2, 10, '2023-12-16'),
(3, 'Drinks', 'Coke', 'Best', 0, 15, '2023-12-16');

-- --------------------------------------------------------

--
-- Table structure for table `recordstable`
--

CREATE TABLE `recordstable` (
  `recordDate` date NOT NULL,
  `sold` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `recordstable`
--

INSERT INTO `recordstable` (`recordDate`, `sold`) VALUES
('2023-12-15', 2),
('2023-12-18', 21),
('2023-12-19', 5),
('2023-12-20', 8);

-- --------------------------------------------------------

--
-- Table structure for table `userstable`
--

CREATE TABLE `userstable` (
  `userId` varchar(45) NOT NULL,
  `firstname` varchar(45) NOT NULL,
  `lastname` varchar(45) NOT NULL,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `birthdate` date NOT NULL,
  `gender` varchar(45) NOT NULL,
  `profileImgPath` varchar(45) NOT NULL,
  `userType` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `userstable`
--

INSERT INTO `userstable` (`userId`, `firstname`, `lastname`, `username`, `password`, `birthdate`, `gender`, `profileImgPath`, `userType`) VALUES
('superuser.wan', 'Josuan', 'Hulom', 'admin@231', '123', '2004-02-21', 'Male', 'admin.jpg', 0),
('user001.wan', 'Ethel', 'Densing', 'ethel', '001', '2004-05-07', 'Female', 'ethel.jpg', 1),
('user002.wan', 'Kylle', 'Alino', 'kylle', '002', '2004-11-07', 'Male', 'kylle.jpg', 1),
('user003.wan', 'Angela', 'Lepiten', 'angela@231', 'Angela@231', '2023-12-20', 'Female', '2023-12-20_angela.jpg', 4);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `apptable`
--
ALTER TABLE `apptable`
  ADD PRIMARY KEY (`appID`);

--
-- Indexes for table `categorytable`
--
ALTER TABLE `categorytable`
  ADD PRIMARY KEY (`categoryID`),
  ADD UNIQUE KEY `categoryName_UNIQUE` (`categoryName`);

--
-- Indexes for table `inventorytable`
--
ALTER TABLE `inventorytable`
  ADD PRIMARY KEY (`productID`),
  ADD UNIQUE KEY `ProductName_UNIQUE` (`ProductName`);

--
-- Indexes for table `recordstable`
--
ALTER TABLE `recordstable`
  ADD PRIMARY KEY (`recordDate`);

--
-- Indexes for table `userstable`
--
ALTER TABLE `userstable`
  ADD PRIMARY KEY (`userId`),
  ADD UNIQUE KEY `username_UNIQUE` (`username`),
  ADD UNIQUE KEY `userId_UNIQUE` (`userId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `apptable`
--
ALTER TABLE `apptable`
  MODIFY `appID` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `categorytable`
--
ALTER TABLE `categorytable`
  MODIFY `categoryID` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `inventorytable`
--
ALTER TABLE `inventorytable`
  MODIFY `productID` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
