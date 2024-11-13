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

    console.log('Booking Data:', bookingData); // Add this line

    // Compose the email
    const mailOptions = {
      from: senderEmail,
      to: adminEmail,
      subject: 'New Booking Received',
      html: `<p>A new booking has been made:</p>
             <p><strong>User Name:</strong> ${bookingData.userName || 'N/A'}</p>
             <p><strong>User Email:</strong> ${bookingData.userEmail || 'N/A'}</p>
             <p><strong>Room:</strong> ${bookingData.room || 'N/A'}</p>
             <p><strong>Date:</strong> ${bookingData.date || 'N/A'}</p> <!-- Changed "Day" to "Date" -->
             <p><strong>Time:</strong> ${bookingData.time || 'N/A'}</p>
             <p><strong>Company:</strong> ${bookingData.userCompany || 'N/A'}</p>
             <p><strong>Phone:</strong> ${bookingData.userPhone || 'N/A'}</p>
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

// Cloud Function to send email on booking status update
exports.sendUserNotification = functions.firestore
  .document('Bookings/{bookingId}')
  .onUpdate(async (change, context) => {
    console.log('Function triggered on update');
    const beforeData = change.before.data();
    const afterData = change.after.data();

    console.log('Before Data:', beforeData);
    console.log('After Data:', afterData);

    // Check if the booking status has changed
    if (beforeData.status !== afterData.status) {
      console.log(`Status changed from ${beforeData.status} to ${afterData.status}`);

      let subject, htmlContent;

      if (afterData.status === 'confirmed') {
        subject = 'Booking Confirmed';
        htmlContent = `<p>Your booking has been confirmed:</p>
                       <p><strong>Room:</strong> ${afterData.room || 'N/A'}</p>
                       <p><strong>Date:</strong> ${afterData.date || 'N/A'}</p>
                       <p><strong>Time:</strong> ${afterData.time || 'N/A'}</p>
                       <p>Thank you for booking with us!</p>`;
      } else if (afterData.status === 'rejected') {
        subject = 'Booking Rejected';
        htmlContent = `<p>Your booking has been rejected:</p>
                       <p><strong>Room:</strong> ${afterData.room || 'N/A'}</p>
                       <p><strong>Date:</strong> ${afterData.date || 'N/A'}</p>
                       <p><strong>Time:</strong> ${afterData.time || 'N/A'}</p>
                       <p>We are sorry, but your booking could not be accommodated.</p>`;
      }

      if (subject && htmlContent) {
        const mailOptions = {
          from: senderEmail,
          to: afterData.userEmail,
          subject: subject,
          html: htmlContent,
        };

        try {
          await transporter.sendMail(mailOptions);
          console.log(`${subject} email sent to user:`, afterData.userEmail);
        } catch (error) {
          console.error('Error sending email:', error);
        }
      } else {
        console.log('No email sent. Status did not change to confirmed or rejected.');
      }
    } else {
      console.log('No status change detected.');
    }
  });