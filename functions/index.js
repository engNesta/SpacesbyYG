// functions/index.js

const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');

// Initialize Firebase Admin
admin.initializeApp();

// Retrieve email credentials from environment variables
const adminEmail = functions.config().email.admin;
const senderEmail = functions.config().email.user;
const senderPassword = functions.config().email.pass;

// Configure Nodemailer transporter
let transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: senderEmail,
    pass: senderPassword,
  },
});

// Cloud Function to send email on new booking
exports.sendAdminNotification = functions.firestore
  .document('Bookings/{bookingId}')
  .onCreate(async (snap, context) => {
    const bookingData = snap.data(); // Get the document data

    // Compose the email
    const mailOptions = {
      from: senderEmail,
      to: adminEmail,
      subject: 'New Booking Received',
      html: `<p>A new booking has been made:</p>
             <p><strong>User Name:</strong> ${bookingData.userName || 'N/A'}</p>
             <p><strong>User Email:</strong> ${bookingData.userEmail || 'N/A'}</p>
             <p><strong>Room:</strong> ${bookingData.room || 'N/A'}</p>
             <p><strong>Day:</strong> ${bookingData.day || 'N/A'}</p>
             <p><strong>Time:</strong> ${bookingData.time || 'N/A'}</p>
             <p><strong>Status:</strong> ${bookingData.status || 'pending'}</p>
             <p>Please log in to the admin portal to review and confirm the booking.</p>`,
    };

    try {
      await transporter.sendMail(mailOptions);
      console.log('Email sent to admin:', adminEmail);
    } catch (error) {
      console.error('Error sending email:', error);
    }
  });
